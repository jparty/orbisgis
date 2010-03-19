package org.orbisgis.core.ui.pluginSystem.workbench;

import java.io.File;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.pluginSystem.PlugInManager;
import org.orbisgis.core.ui.pluginSystem.WorkbenchProperties;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;

//create WorkbenchContext
public class OrbisWorkbench {

	private WorkbenchContext context;
	private PlugInManager plugInManager;
	private WorkbenchProperties properties;

	public PlugInManager getPlugInManager() {
		return plugInManager;
	}

	private OrbisGISFrame frame;

	public OrbisWorkbench(OrbisGISFrame frame) {
		context = new OrbisWorkbenchContext(this);
		Services.registerService(WorkbenchContext.class,
				"Gives access to the current WorkbenchContext", this.context);
		this.frame = frame;
	}

	public OrbisWorkbench() {
		context = new OrbisWorkbenchContext(this);
	}

	public void runWorkbench() {
		File extensionsDirectory = new File("lib/ext");
		/*
		 * File defaultPlugins = new File("default-plugins.xml"); try {
		 * properties = new WorkbenchPropertiesFile(defaultPlugins); } catch
		 * (JDOMException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		OrbisConfiguration setup = new OrbisConfiguration();
		try {
			plugInManager = new PlugInManager(context, extensionsDirectory);
			setup.setup(context);
			context.getWorkbench().getPlugInManager().load();
			context.setLastAction("Orbisgis started");
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public OrbisGISFrame getFrame() {
		return frame;
	}

	public WorkbenchContext getWorkbenchContext() {
		return context;
	}

	public WorkbenchProperties getProperties() {
		return properties;
	}
}