package org.nuxeo.labs.pst.work;

import org.nuxeo.ecm.core.transientstore.work.TransientStoreWork;

public class PSTImportWork extends TransientStoreWork {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String key;
    
    public PSTImportWork() {
        super();
    }

    public PSTImportWork(String id) {
        super(id);
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void work() {
        // TODO Auto-generated method stub

    }

    @Override
    public void cleanUp(boolean ok, Exception e) {
        // TODO Auto-generated method stub
        super.cleanUp(ok, e);
    }
    
    

}
