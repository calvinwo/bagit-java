package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.workflow.BaseHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Agents.*;


public class AddStorageSystemFileLocationActionHandlerTest extends BaseHandlerTest {

	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='b' />" +
	      "    <event type='node-leave'>" +
	      "      <action name='add staging file location' class='gov.loc.repository.workflow.actionhandlers.AddStorageSystemFileLocationActionHandler'>" +
	      "        <basePathVariable>stagingBasePath</basePathVariable>" +
	      "        <storageSystemIdVariable>stagingStorageSystemId</storageSystemIdVariable>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      	      
	      "  </start-state>" +
	      "  <end-state name='b' />" +
	      "</process-definition>");
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	    processInstance.getContextInstance().setVariable("stagingBasePath", BASEPATH_1);
	    processInstance.getContextInstance().setVariable("stagingStorageSystemId", RDC);
	    
	    this.commitAndRestartTransaction();
	    
	    //Gets out of start state
	    processInstance.signal();

	    this.commitAndRestartTransaction();
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());	    
	    Package packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
	    StorageSystemFileLocation fileLocation = packge.getFileLocation(RDC, BASEPATH_1);
	    assertNotNull(fileLocation);
	    assertTrue(fileLocation.isManaged());
	    assertTrue(fileLocation.isLCPackageStructure());
	}

	
}
