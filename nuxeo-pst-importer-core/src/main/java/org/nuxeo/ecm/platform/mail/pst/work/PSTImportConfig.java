package org.nuxeo.ecm.platform.mail.pst.work;

import java.io.Serializable;
import java.util.function.Predicate;

import org.nuxeo.common.utils.Path;

import com.pff.PSTMessage;
import com.pff.PSTObject;

public class PSTImportConfig implements Serializable, Predicate<PSTObject> {

    private static final long serialVersionUID = 1L;

    public static class Builder {

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

        public Builder attachments(boolean value) {
            attachments = value;
            return this;
        }

        public Builder messages(boolean value) {
            messages = value;
            return this;
        }

        public Builder activity(boolean value) {
            activity = value;
            return this;
        }

        public Builder appointment(boolean value) {
            appointment = value;
            return this;
        }

        public Builder contact(boolean value) {
            contact = value;
            return this;
        }

        public Builder distributionList(boolean value) {
            distributionList = value;
            return this;
        }

        public Builder rss(boolean value) {
            rss = value;
            return this;
        }

        public Builder task(boolean value) {
            task = value;
            return this;
        }

        public Builder emptyFolders(boolean value) {
            emptyFolders = value;
            return this;
        }

        public Builder commitThreshold(int value) {
            commitThreshold = value;
            return this;
        }

        public Builder parent(Path value) {
            parent = value;
            return this;
        }

        public PSTImportConfig build() {
            PSTImportConfig config = new PSTImportConfig();
            config.attachments = attachments;
            config.messages = messages;
            config.activity = activity;
            config.appointment = appointment;
            config.contact = contact;
            config.distributionList = distributionList;
            config.rss = rss;
            config.task = task;
            config.emptyFolders = emptyFolders;
            config.commitThreshold = commitThreshold;
            config.parent = parent;
            return config;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

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

    protected PSTImportConfig() {
        super();
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
