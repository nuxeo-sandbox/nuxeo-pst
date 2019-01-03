package org.nuxeo.labs.pst;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({ "org.nuxeo.ecm.platform.mail.types", "org.nuxeo.ecm.platform.mail",
        "org.nuxeo.labs.pst.nuxeo-pst-importer-core" })
public class TestImportPST {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Test
    public void shouldCallWithParameters() throws OperationException {
        final String path = "/";
        OperationContext ctx = new OperationContext(session);

        File f = FileUtils.getResourceFileFromContext("dist-list.pst");
        FileBlob blob = new FileBlob(f);
        ctx.setInput(blob);

        Map<String, Object> params = new HashMap<>();
        params.put("destination", path);
        params.put("async", false);
        Object output = automationService.run(ctx, ImportPST.ID, params);

        assertEquals(blob, output);
    }
    
    @Test
    public void testEnronPst() throws OperationException {
        final String path = "/";
        OperationContext ctx = new OperationContext(session);

        File f = FileUtils.getResourceFileFromContext("albert_meyers_000_1_1.pst");
        FileBlob blob = new FileBlob(f);
        ctx.setInput(blob);

        Map<String, Object> params = new HashMap<>();
        params.put("destination", path);
        params.put("async", false);
        Object output = automationService.run(ctx, ImportPST.ID, params);

        assertEquals(blob, output);
    }
}
