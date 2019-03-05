package org.nuxeo.ecm.platform.mail.pst.work;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;
import java.util.UUID;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.util.IOUtils;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.automation.core.util.DocumentHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.api.model.Property;
import org.nuxeo.ecm.core.blob.ManagedBlob;
import org.nuxeo.ecm.core.transientstore.work.TransientStoreWork;
import org.nuxeo.runtime.api.Framework;

import com.pff.PSTAttachment;
import com.pff.PSTException;
import com.pff.PSTFile;
import com.pff.PSTFolder;
import com.pff.PSTMessage;
import com.pff.PSTObject;
import com.pff.PSTRecipient;

public class PSTImportWork extends TransientStoreWork {

    private static final long serialVersionUID = 1L;

    protected static Log LOG = LogFactory.getLog(PSTImportWork.class);

    protected PSTImportConfig config = new PSTImportConfig();

    protected String inputEntryKey;

    /**
     * @since 10.1
     */
    protected ManagedBlob managedBlob;

    private int depth = -1;

    private int saved = 0;

    protected Stack<String> folders = new Stack<>();

    protected String folderishType = "PSTFolder";

    protected String messageType = "PSTMessage";

    public PSTImportWork() {
        super();
    }

    public PSTImportWork(String id, BlobHolder blobHolder, PSTImportConfig config) {
        super(id);

        if (config == null) {
            throw new NullPointerException("Configuration missing for PST Import");
        }

        this.config = config;
        storeInputBlobHolder(blobHolder);
    }

    protected void storeInputBlobHolder(BlobHolder blobHolder) {
        if (!storeManagedBlob(blobHolder)) {
            // standard conversion
            inputEntryKey = entryKey + "_input";
            putBlobHolder(inputEntryKey, blobHolder);
        }
    }

    /**
     * @since 10.1
     */
    protected boolean storeManagedBlob(BlobHolder blobHolder) {
        Blob blob = blobHolder.getBlob();
        if (!(blob instanceof ManagedBlob) || !(blob instanceof Serializable) || blobHolder.getBlobs().size() > 1) {
            return false;
        }

        ManagedBlob mBlob = (ManagedBlob) blob;
        this.managedBlob = mBlob;
        return true;

    }

    protected BlobHolder retrieveInputBlobHolder() {
        return managedBlob != null ? new SimpleBlobHolder(managedBlob) : getBlobHolder(inputEntryKey);
    }

    @Override
    public void cleanUp(boolean ok, Exception e) {
        super.cleanUp(ok, e);
        if (inputEntryKey != null) {
            removeBlobHolder(inputEntryKey);
        }
    }

    @Override
    public String getTitle() {
        return "PST Importer";
    }

    @Override
    public void work() {
        setStatus("Importing");
        setProgress(Progress.PROGRESS_0_PC);

        BlobHolder inputBlobHolder = retrieveInputBlobHolder();
        if (inputBlobHolder == null) {
            setStatus(null);
            return;
        }

        for (Blob data : inputBlobHolder.getBlobs()) {
            try {
                openSystemSession();
                processBlob(data);
                commit();
            } catch (PSTException | IOException e) {
                LOG.error("Error processing PST import", e);
            } finally {
                closeSession();
            }
        }

        setProgress(Progress.PROGRESS_100_PC);
        setStatus(null);
    }

    protected void processBlob(Blob data) throws IOException, PSTException {
        File pstFile = data.getFile();
        boolean tempFile = false;
        if (pstFile == null) {
            pstFile = Framework.createTempFile("pst", "_import.pst");
            tempFile = true;
            try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(pstFile))) {
                IOUtils.copy(data.getStream(), fout);
            }
        }

        PSTFile reader = new PSTFile(pstFile);
        processFolder(config.getParent(), reader.getRootFolder());
        if (tempFile) {
            pstFile.delete();
        }
    }

    protected void processFolder(Path parent, PSTFolder folder) throws PSTException, IOException {
        this.depth++;
        // the root folder doesn't have a display name
        Path container = parent;

        if (this.depth > 0) {
            folders.push(folder.getDisplayName());
            container = createFolder(container, folder);
        }

        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                if (config.emptyFolders || childFolder.hasSubfolders() || childFolder.getContentCount() > 0) {
                    processFolder(container, childFolder);
                }
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
            this.depth++;
            PSTObject child = folder.getNextChild();
            while (child != null) {
                // Filter messages
                if (config.test(child)) {
                    // Add message
                    createObject(container, child);
                }

                // Get next message
                child = folder.getNextChild();
            }
            this.depth--;
        }

        if (this.depth > 0) {
            folders.pop();
        }
        this.depth--;
    }

    protected boolean isValidName(String name) {
        return StringUtils.isNotBlank(nullStrip(name));
    }

    protected String nullStrip(String name) {
        return StringUtils.remove(name, '\0');
    }

    protected String getValidName(String name) {
        name = nullStrip(name);
        name = StringUtils.replaceChars(name, '\\', '-');
        name = StringUtils.replaceChars(name, '/', '-');
        return name;
    }

    private Path createFolder(Path container, PSTFolder folder) {
        // We need to create a folderish
        String folderName = folder.getDisplayName();
        if (isValidName(folderName)) {
            folderName = getValidName(folderName);
        } else {
            folderName = "Folder";
        }

        DocumentModel folderDoc = session.createDocumentModel(container.toString(), folderName, folderishType);
        folderDoc.setPropertyValue("dc:title", folderName);
        folderDoc = session.createDocument(folderDoc);
        folderDoc = session.saveDocument(folderDoc);
        savedSomething();
        return folderDoc.getPath();
    }

    private DocumentModel createObject(Path container, PSTObject child) {
        // We need to create a widget
        PSTMessage msg = (PSTMessage) child;
        String itemName = null;
        if (isValidName(msg.getSubject())) {
            itemName = getValidName(msg.getSubject());
        } else {
            itemName = "Mail Message (Subject Unreadable)";
        }

        DocumentModel pstDoc = session.createDocumentModel(container.toString(), itemName, messageType);
        pstDoc.setPropertyValue("dc:title", itemName);
        try {
            populateMessage(msg, pstDoc);
            pstDoc = session.createDocument(pstDoc);
            pstDoc = session.saveDocument(pstDoc);
            savedSomething();
            return pstDoc;
        } catch (PSTException | IOException e) {
            LOG.error("Error populating message", e);
        }
        return null;
    }

    private void populateMessage(PSTMessage msg, DocumentModel pstDoc) throws PSTException, IOException {
        Property attachProp = pstDoc.getProperty("files:files");

        pstDoc.setPropertyValue("mail:messageId", msg.getInternetMessageId());
        pstDoc.setPropertyValue("mail:sender", msg.getSenderEmailAddress());
        pstDoc.setPropertyValue("mail:sending_date", msg.getMessageDeliveryTime());
        pstDoc.setPropertyValue("mail:recipients", addressList(msg, PSTMessage.RECIPIENT_TYPE_TO));
        pstDoc.setPropertyValue("mail:cc_recipients", addressList(msg, PSTMessage.RECIPIENT_TYPE_CC));
        if (StringUtils.isNotBlank(msg.getBody())) {
            pstDoc.setPropertyValue("mail:text", msg.getBody());
        }
        if (StringUtils.isNotBlank(msg.getBodyHTML())) {
            pstDoc.setPropertyValue("mail:htmlText", msg.getBodyHTML());
        }
        if (StringUtils.isNotBlank(msg.getRTFBody())) {
            Blob ba = Blobs.createBlob(msg.getRTFBody(), "application/rtf");
            ba.setFilename("message_body.rtf");
            DocumentHelper.addBlob(attachProp, ba);
        }

        if (msg.getConversationIndex() != null) {
            UUID cid = msg.getConversationIndex().getGuid();
            if (cid != null) {
                pstDoc.setPropertyValue("pst:conversation", cid.toString());
            }
        }

        pstDoc.setPropertyValue("pst:priority", priorityToString(msg.getPriority()));
        pstDoc.setPropertyValue("pst:replied", msg.hasReplied());
        pstDoc.setPropertyValue("pst:forwarded", msg.hasForwarded());
        pstDoc.setPropertyValue("pst:flagged", msg.isFlagged());
        pstDoc.setPropertyValue("pst:fromSender", msg.isFromMe());
        pstDoc.setPropertyValue("pst:read", msg.isRead());
        pstDoc.setPropertyValue("pst:unsent", msg.isUnsent());
        pstDoc.setPropertyValue("pst:categories", msg.getColorCategories());

        if (config.attachments && msg.hasAttachments()) {
            int attach = msg.getNumberOfAttachments();
            for (int i = 0; i < attach; i++) {
                PSTAttachment a = msg.getAttachment(i);
                Blob ba = Blobs.createBlob(a.getFileInputStream(), nullStrip(a.getMimeTag()));
                ba.setFilename(nullStrip(a.getFilename()));
                DocumentHelper.addBlob(attachProp, ba);
            }
        }
    }

    private String priorityToString(int val) {
        if (val <= PSTMessage.IMPORTANCE_LOW) {
            return "Low";
        } else if (val >= PSTMessage.IMPORTANCE_HIGH) {
            return "High";
        }
        return "Normal";
    }

    private ArrayList<String> addressList(PSTMessage msg, int recipientType) throws PSTException, IOException {
        ArrayList<String> list = new ArrayList<>();
        int recip = msg.getNumberOfRecipients();
        for (int i = 0; i < recip; i++) {
            PSTRecipient rec = msg.getRecipient(i);
            if (rec.getRecipientType() == recipientType) {
                list.add(nullStrip(rec.getEmailAddress()));
            }
        }
        return list;
    }

    private void savedSomething() {
        if (++saved == config.commitThreshold) {
            saved = 0;
            commit();
        }
    }

    private void commit() {
        commitOrRollbackTransaction();
        startTransaction();
    }

}
