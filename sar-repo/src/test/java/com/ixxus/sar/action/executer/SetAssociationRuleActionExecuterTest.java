package com.ixxus.sar.action.executer;

import com.ixxus.sar.model.SARContentModel;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

import com.tradeshift.test.remote.Remote;
import com.tradeshift.test.remote.RemoteTestRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Ixxus on 11/9/2015.
 */
@RunWith(RemoteTestRunner.class)
@Remote(runnerClass=SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:alfresco/application-context.xml")
public class SetAssociationRuleActionExecuterTest {

    @Autowired
    @Qualifier("repositoryHelper")
    protected Repository repository;

    @Autowired
    @Qualifier("dictionaryService")
    protected DictionaryService dictionaryService;

    @Autowired
    @Qualifier("NodeService")
    protected NodeService nodeService;

    @Autowired
    @Qualifier("set-association-rule")
    protected SetAssociationRuleActionExecuter actionExecuter;

    private NodeRef testHomeFolder, contentA, contentB, folderA, folderB;
    private Action action;

    @Before
    public void setUp() throws Exception {
        // do some setting up
        AuthenticationUtil.setFullyAuthenticatedUser("admin");
        deleteTestFolderIfExists();
        assertNotNull(nodeService);
        assertNotNull(dictionaryService);
        assertNotNull(repository);
        // create test home folder
        testHomeFolder = createFolderInCompanyHome("test");
        // create associations targets
        contentA = createContent(testHomeFolder, "content-a", null, ContentModel.TYPE_CONTENT);
        contentB = createContent(testHomeFolder, "content-b", null, ContentModel.TYPE_CONTENT);
        folderA = createContent(testHomeFolder, "folder-a", null, ContentModel.TYPE_FOLDER);
        folderB = createContent(testHomeFolder, "folder-b", null, ContentModel.TYPE_FOLDER);

        setupAction();

    }

    @After
    public void tearDown() throws Exception {
        if (null != testHomeFolder && nodeService.exists(testHomeFolder)) {
            nodeService.deleteNode(testHomeFolder);
        }
    }

    /*
        Test Successfully Applying Association
        Set RelatedToContent on a cm:content node type
        to another cm:content node type
     */
    @Test
    public void testSuccessfulSetRelatedToContentAssociation() {
        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(contentA));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testSuccessfulSetRelatedToContentAssociation",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        assertHasTargetAssocTo("Has RelatedToContent Association", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentA);

    }

    /*
        Test Successfully Applying Association
        Set RelatedToContent on a cm:content node type
        to multiple other cm:content node types
     */
    @Test
    public void testSuccessfulSetRelatedToContentAssociations() {
        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(contentA, contentB));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testSuccessfulSetRelatedToContentAssociations",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        assertHasTargetAssocTo("Has RelatedToContent Association to ContentA", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentA);
        assertHasTargetAssocTo("Has RelatedToContent Association to ContentB", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentB);

    }

    /*
        Test Successfully Applying Association
        Set RelatedToContent on a cm:content node type
        to another cm:content node type
     */
    @Test
    public void testSuccessfulSetRelatedToFolderAssociation() {
        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(folderA));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testSuccessfulSetRelatedToFolderAssociation",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        assertHasTargetAssocTo("Has RelatedToFolder Association", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME, folderA);

    }

    /*
        Test Successfully Applying Association
        Set RelatedToContent on a cm:content node type
        to multiple other cm:content node types
     */
    @Test
    public void testSuccessfulSetRelatedToFolderAssociations() {
        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(folderA, folderB));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testSuccessfulSetRelatedToFolderAssociations",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        assertHasTargetAssocTo("Has RelatedToContent Association to FolderA", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME, folderA);
        assertHasTargetAssocTo("Has RelatedToContent Association to FolderB", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME, folderB);

    }

    /*
        Test Unsuccessfully Applying Association
        Set RelatedToContent on a cm:content node type
        to cm:folder node type
     */
    @Test
    public void testUnsuccessfulSetAssociationWrongType() {
        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(folderA));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testUnsuccessfulSetAssociationWrongType",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        // the association was not set because we tried setting a Folder type
        // as the target of a RelatedToContent association
        assertHasNoTargetAssocTo("Has No RelatedToContent Association", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, folderA);


    }

    /*
        Test Unsuccessfully Applying Association
        Set RelatedToContent on a cm:content node type
        that doesn't have the RelatedTo aspect applied
     */
    @Test
    public void testUnsuccessfulSetAssociationNoAspect() {

        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(folderA));

        // create a new node to test this on
        NodeRef testNode = createContent(testHomeFolder, "testSuccessfulSetRelatedToFolderAssociation",
                null, ContentModel.TYPE_CONTENT);
        // test that it doesn't have the aspect
        // causing the association to not be applied later
        Assert.assertEquals("Does this node have the proper aspect applied?",
                false, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association
        assertHasNoTargetAssocTo("Has No RelatedToFolder Association", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToFolder.QNAME, folderA);
    }

    /*
        Test  Applying Association Where It Already Exists
        Set RelatedToContent on a cm:content node type
        that already has the same existing association to
        another cm:content type
     */
    @Test
    public void testNoExceptionOnAssociationAlreadyExists() {

        // setup a RelatedToContent action
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_NAME))
                .thenReturn(SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME);
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(contentA));

        // create a new node to test this on
        NodeRef testNode = createSpecialisedContent(testHomeFolder, "testSuccessfulSetRelatedToContentAssociations",
                null, ContentModel.TYPE_CONTENT);
        Assert.assertEquals("Does this node have the proper aspect applied?",
                true, nodeService.hasAspect(testNode, SARContentModel.Aspect.RelatedTo.QNAME));

        // apply action
        actionExecuter.executeImpl(action, testNode);

        // test for new association on first pass
        assertHasTargetAssocTo("Has RelatedToContent Association to ContentA", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentA);
        assertHasNoTargetAssocTo("Has No RelatedToContent Association to ContentB", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentB);

        // now give it more association targets
        when(action.getParameterValue(SetAssociationRuleActionExecuter.PARAM_ASSOCIATION_VALUE))
                .thenReturn((Serializable)Arrays.asList(contentA, contentB));
        // apply action again, with  more targets
        actionExecuter.executeImpl(action, testNode);

        // test for new association on second pass
        assertHasTargetAssocTo("Has RelatedToContent Association to ContentA", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentA);
        assertHasTargetAssocTo("Now... Has RelatedToContent Association to ContentB", testNode,
                SARContentModel.Aspect.RelatedTo.Assoc.RelatedToContent.QNAME, contentB);
    }

    protected void setupAction() {
        action = mock(Action.class);
        when(action.getParameterValues()).thenReturn(new HashMap<>());
    }

    protected void assertHasTargetAssocTo(String message, NodeRef source, QName assocType, NodeRef target) {
        assertTrue(message, hasAssocs(source, assocType, target));
    }
    protected void assertHasNoTargetAssocTo(String message, NodeRef source, QName assocType, NodeRef target) {
        assertFalse(message, hasAssocs(source, assocType, target));
    }

    private boolean hasAssocs(NodeRef source, QName assocType, NodeRef target) {
        return nodeService.getTargetAssocs(source, assocType)
                .stream()
                .filter(associationRef -> associationRef.getTargetRef().toString().equals(target.toString()))
                .findFirst()
                .isPresent();
    }

    protected NodeRef createFolderInCompanyHome(String folderName) {
        AuthenticationUtil.setFullyAuthenticatedUser("admin");
        NodeRef companyHomeNodeRef = repository.getCompanyHome();

        return createContent(companyHomeNodeRef, folderName, null, ContentModel.TYPE_FOLDER);
    }
    protected NodeRef createSpecialisedContent(NodeRef parentNodeRef, String contentName,
            Map<QName, Serializable> properties, QName contentTypeQName) {
        NodeRef content = createContent(parentNodeRef, contentName, properties, contentTypeQName);
        nodeService.addAspect(content, SARContentModel.Aspect.RelatedTo.QNAME, null);
        return content;
    }
    protected NodeRef createContent(NodeRef parentNodeRef, String contentName,
            Map<QName, Serializable> properties,
            QName contentTypeQName) {
        if (properties == null) {
            properties = new HashMap<QName, Serializable>();
        }
        properties.put(ContentModel.PROP_NAME, contentName);
        return nodeService.createNode(parentNodeRef, ContentModel.ASSOC_CONTAINS,
                QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, contentName),
                contentTypeQName, properties).getChildRef();
    }
    protected void deleteTestFolderIfExists() {
        NodeRef companyHomeNodeRef = repository.getCompanyHome();
        List<ChildAssociationRef> children = nodeService.getChildrenByName(companyHomeNodeRef, ContentModel.ASSOC_CONTAINS, Arrays.asList("test"));
        if (!children.isEmpty()) {
            ChildAssociationRef test = children.get(0);
            if (test != null) {
                nodeService.deleteNode(test.getChildRef());
            }
        }
    }
}