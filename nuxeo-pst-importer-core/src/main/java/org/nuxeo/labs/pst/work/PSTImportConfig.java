package org.nuxeo.labs.pst.work;

import java.io.Serializable;
import java.util.function.Predicate;

import org.nuxeo.common.utils.Path;

import com.pff.PSTMessage;
import com.pff.PSTObject;

public class PSTImportConfig implements Serializable, Predicate<PSTObject> {

    private static final long serialVersionUID = 1L;

    protected boolean attachments = true;

    protected boolean messages = true;

    protected boolean activity = false;

    protected boolean appointment = false;

    protected boolean contact = false;

    protected boolean distributionList = false;

    protected boolean rss = false;

    protected boolean task = false;
    
    protected boolean emptyFolders = false;

    protected int commitThreshold = 100;

    protected Path parent = null;

    public PSTImportConfig() {
        super();
    }

    public PSTImportConfig(boolean attachments, boolean messages, boolean activity, boolean appointment,
            boolean contact, boolean distributionList, boolean rss, boolean task, boolean emptyFolders) {
        super();
        this.attachments = attachments;
        this.messages = messages;
        this.activity = activity;
        this.appointment = appointment;
        this.contact = contact;
        this.distributionList = distributionList;
        this.rss = rss;
        this.task = task;
        this.emptyFolders = emptyFolders;
    }

    public int getCommitThreshold() {
        return commitThreshold;
    }

    public void setCommitThreshold(int commitThreshold) {
        this.commitThreshold = commitThreshold;
    }

    public Path getParent() {
        return parent;
    }

    public void setParent(Path parent) {
        this.parent = parent;
    }

    @Override
    public boolean test(PSTObject t) {
        return t.getClass() == PSTMessage.class;
    }

}
