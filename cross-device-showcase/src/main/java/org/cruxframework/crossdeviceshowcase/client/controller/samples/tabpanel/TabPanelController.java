package org.cruxframework.crossdeviceshowcase.client.controller.samples.tabpanel;

import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.views.BindView;
import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox.MessageType;
import org.cruxframework.crux.smartfaces.client.tab.TabPanel;

@Controller("tabPanelController")
public class TabPanelController 
{
	@Inject
	private MyWidgetAccessor myWidgetAccessor;

	@Inject
	private TabPanelMessages messages;

	@Expose
	public void onLoad()
	{
		myWidgetAccessor.tabPanel().selectTab(0);
	}
	
	@Expose
	public void onClickButton()
	{
		MessageBox.show(messages.clickTab1(), MessageType.INFO);
	}
	
	@Expose
	public void onClickTab2()
	{
		MessageBox.show(messages.clickTab2(), MessageType.INFO);
	}
	
	@Expose
	public void onClickTab3()
	{
		MessageBox.show(messages.clickTab3(), MessageType.INFO);
	}

	@BindView("tabPanel")
	public interface MyWidgetAccessor extends WidgetAccessor
	{
		TabPanel tabPanel();
	}

	public void setMyWidgetAccessor(MyWidgetAccessor myWidgetAccessor) 
	{
		this.myWidgetAccessor = myWidgetAccessor;
	}

	public void setMessages(TabPanelMessages messages) 
	{
		this.messages = messages;
	}
}

