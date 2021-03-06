package org.nuxeo.ecm.platform.mail.pst;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.automation.core.collectors.BlobCollector;
import org.nuxeo.ecm.automation.core.collectors.DocumentModelCollector;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.DocumentBlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.mail.pst.work.PSTImportConfig;
import org.nuxeo.ecm.platform.mail.pst.work.PSTImportWork;

/**
 *
 */
@Operation(id = ImportPST.ID, category = Constants.CAT_DOCUMENT, label = "Import PST", description = "Import a Microsoft Outlook compatible message archive.")
public class ImportPST {

    public static final String ID = "Document.ImportPST";

    @Context
    protected CoreSession session;

    @Context
    protected WorkManager service;

    @Param(name = "xpath", required = false, values = { "file:content" })
    protected String xpath;

    @Param(name = "destination", required = true)
    protected String destination;

    @Param(name = "async", required = false, values = { "true" })
    protected boolean async = true;

    @Param(name = "import.attachments", required = false, values = { "true" })
    protected boolean importAttachments = true;

    @Param(name = "import.messages", required = false, values = { "true" })
    protected boolean importMessages = true;

    @Param(name = "import.activity", required = false, values = { "false" })
    protected boolean importActivity = false;

    @Param(name = "import.appointment", required = false, values = { "false" })
    protected boolean importAppointment = false;

    @Param(name = "import.contact", required = false, values = { "false" })
    protected boolean importContact = false;

    @Param(name = "import.distributionList", required = false, values = { "false" })
    protected boolean importDistributionList = false;

    @Param(name = "import.rss", required = false, values = { "false" })
    protected boolean importRss = false;

    @Param(name = "import.task", required = false, values = { "false" })
    protected boolean importTask = false;

    @Param(name = "import.emptyFolders", required = false, values = { "false" })
    protected boolean importEmptyFolders = false;

    @OperationMethod(collector = DocumentModelCollector.class)
    public DocumentModel run(DocumentModel doc) {
        BlobHolder bh = null;
        if (StringUtils.isNotBlank(xpath)) {
            bh = new DocumentBlobHolder(doc, xpath);
        } else {
            bh = doc.getAdapter(BlobHolder.class);
        }
        runWorker(bh);
        return doc;
    }

    @OperationMethod(collector = BlobCollector.class)
    public Blob run(Blob blob) throws IOException, OperationException {
        runWorker(new SimpleBlobHolder(blob));
        return blob;
    }

    protected void runWorker(BlobHolder bh) {
        DocumentModel parent = null;
        if (StringUtils.isBlank(destination)) {
            parent = session.getRootDocument();
        } else {
            parent = session.getDocument(
                    destination.startsWith("/") ? new PathRef(destination) : new IdRef(destination));
        }

        PSTImportConfig config = PSTImportConfig.builder()
                                                .attachments(importAttachments)
                                                .messages(importMessages)
                                                .activity(importActivity)
                                                .appointment(importAppointment)
                                                .contact(importContact)
                                                .distributionList(importDistributionList)
                                                .rss(importRss)
                                                .task(importTask)
                                                .emptyFolders(importEmptyFolders)
                                                .parent(parent.getPath())
                                                .build();

        PSTImportWork work = new PSTImportWork(UUID.randomUUID().toString(), bh, config);
        if (async) {
            service.schedule(work, true);
        } else {
            work.run();
        }

    }
}
