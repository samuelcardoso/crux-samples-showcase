package org.cruxframework.showcasecore.client.controller.showcase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.cruxframework.crux.core.client.Crux;
import org.cruxframework.crux.core.client.controller.Controller;
import org.cruxframework.crux.core.client.controller.Expose;
import org.cruxframework.crux.core.client.ioc.Inject;
import org.cruxframework.crux.core.client.screen.DeviceAdaptive.Size;
import org.cruxframework.crux.core.client.screen.Screen;
import org.cruxframework.crux.core.client.screen.views.BindRootView;
import org.cruxframework.crux.core.client.screen.views.View;
import org.cruxframework.crux.core.client.screen.views.ViewActivateEvent;
import org.cruxframework.crux.core.client.screen.views.ViewActivateHandler;
import org.cruxframework.crux.core.client.screen.views.WidgetAccessor;
import org.cruxframework.crux.core.client.utils.StringUtils;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox;
import org.cruxframework.crux.smartfaces.client.dialog.MessageBox.MessageType;
import org.cruxframework.crux.widgets.client.dialogcontainer.DialogViewContainer;
import org.cruxframework.crux.widgets.client.disposal.menutabsdisposal.MenuTabsDisposal;
import org.cruxframework.crux.widgets.client.disposal.panelchoicedisposal.PanelChoiceDisposal;
import org.cruxframework.crux.widgets.client.filter.Filter;
import org.cruxframework.crux.widgets.client.filter.Filterable;
import org.cruxframework.crux.widgets.client.swappanel.HorizontalSwapPanel.Direction;
import org.cruxframework.showcasecore.client.dto.GitResponse;
import org.cruxframework.showcasecore.client.proxy.SourceFilesProxy;
import org.cruxframework.showcasecore.client.resource.ShowcaseCoreMessages;
import org.cruxframework.showcasecore.client.resource.common.ShowcaseResourcesCommon;
import org.cruxframework.showcasecore.client.util.LoadingCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
@SuppressWarnings("deprecation")
@Controller("mainController")
public class MainController 
{
	public static final String RAWTYPE_ENDPOINT = "https://raw.githubusercontent.com/CruxFramework/crux-samples-showcase/master/";
	
	private ShowcaseResourcesCommon bundle = GWT.create(ShowcaseResourcesCommon.class);

	@Inject
	private ShowcaseCoreMessages messages;

	@Inject
	public SourceFilesProxy sourceFilesProxy;
	
	private Logger logger = Logger.getLogger("");

	private LanguageManager languageManager;
	
	private HashMap<String,Integer> mapFilesToRead = new HashMap<String,Integer>();
	
	@Expose
	public void replaceText(AttachEvent event)
	{
		Label label = (Label)event.getSource();
		HTML html = (HTML) View.getView(widgets.menuDisposal().getCurrentView()).getWidget("descriptionCointainer");
		html.setHTML(label.getText());
	}
	
	@Inject
	private MyWidgetAccessor widgets;
	
	public void setWidgets(MyWidgetAccessor widgets)
	{
		this.widgets = widgets;
	}

	@BindRootView
	public static interface MyWidgetAccessor extends WidgetAccessor
	{
		MenuTabsDisposal menuDisposal();
		Filter filter();
		Widget langToggler();
		Widget langEn();
		Widget langPt();
	}
	
	@Expose
	public void switchLocaleUrl()
	{
		Widget langEn = View.of(this).getWidget("langEn");

		if(langEn.getStyleName().contains("active"))
		{
			languageManager.switchLocaleUrl("pt_BR");
		} else
		{
			languageManager.switchLocaleUrl("en_US");
		}
	}

	@Expose
	public void loadShowcaseUrl()
	{
		Window.Location.replace("http://showcase.cruxframework.org/");
	}

	@Expose
	public void onLoad()
	{
		languageManager = new LanguageManager(widgets.langToggler(), widgets.langEn(), widgets.langPt());
		
		//add the event log window
		//if(Screen.getCurrentDevice().getSize().equals(Size.large))
		//{
		//	logger.addHandler(new VisualBoxLogHandler(messages.enableLog(), messages.disableLog()));
		//}

		String hash = Window.Location.getHash();
		
		if(!StringUtils.isEmpty(hash))
		{
			String viewName = hash.split(":")[1];
			
			if(!"wellcome".equals(viewName))
			{
				widgets.menuDisposal().showView(viewName, Direction.FORWARD);
			}
			else
			{
				widgets.menuDisposal().showView("wellcome", Direction.FORWARD);
			}
		}
		else
		{
			widgets.menuDisposal().showView("wellcome", Direction.FORWARD);			
		}

		//Call method to verify browser language
		languageManager.verifyAndToggleLanguage();

		if(Screen.getCurrentDevice().getSize().equals(Size.large))
		{
			//Setup the top filters
			setupFilters();
		}
	}

	private void setupFilters()
	{
		final Filter filter = widgets.filter();
		
		filter.setFilterable(new Filterable<String>()
		{
			@Override
			public List<FilterResult<String>> filter(String query) 
			{	
				List<FilterResult<String>> widgetList = new ArrayList<FilterResult<String>>();
				widgetList.add(new FilterResult<String>("cruxButton", "Button", "cruxButton"));
				widgetList.add(new FilterResult<String>("anchor", "Anchor", "anchor"));
				widgetList.add(new FilterResult<String>("cruxLabel", "Label", "cruxLabel"));
				widgetList.add(new FilterResult<String>("colorPickerDialog", "ColorPicker", "colorPickerDialog"));
				widgetList.add(new FilterResult<String>("date", "Date", "date"));
				widgetList.add(new FilterResult<String>("fileUploader", "FileUploader", "fileUploader"));
				widgetList.add(new FilterResult<String>("maskedLabel", "MaskedLabel", "maskedLabel"));
				widgetList.add(new FilterResult<String>("maskedTextBox", "MaskedTextBox", "maskedTextBox"));
				widgetList.add(new FilterResult<String>("numberTextBox", "NumberTextBox", "numberTextBox"));
				widgetList.add(new FilterResult<String>("textArea", "TextArea", "textArea"));
				widgetList.add(new FilterResult<String>("gwtTextBox", "TextBox", "gwtTextBox"));
				widgetList.add(new FilterResult<String>("singleSelect", "SingleSelect", "singleSelect"));
				widgetList.add(new FilterResult<String>("selectablePanel", "Selectable Panel", "selectablePanel"));
				widgetList.add(new FilterResult<String>("tabPanel", "Tab Panel", "tabPanel"));
				widgetList.add(new FilterResult<String>("menu", "Menu", "menu"));
				widgetList.add(new FilterResult<String>("sideMenuDisposal", "SideMenuDisposal", "sideMenuDisposal"));
				widgetList.add(new FilterResult<String>("topMenuDisposal", "TopMenuDisposal", "topMenuDisposal"));
				widgetList.add(new FilterResult<String>("filter", "Filter", "filter"));
				widgetList.add(new FilterResult<String>("listShuttle", "ListShuttle", "listShuttle"));
				widgetList.add(new FilterResult<String>("sortableList", "SortableList", "sortableList"));
				widgetList.add(new FilterResult<String>("breadcrumb", "Breadcrumb", "breadcrumb"));
				widgetList.add(new FilterResult<String>("datagrid", "DataGRID", "datagrid"));
				widgetList.add(new FilterResult<String>("widgetList", "WidgetList", "widgetList"));
				widgetList.add(new FilterResult<String>("comboBox", "ComboBox", "comboBox"));
				widgetList.add(new FilterResult<String>("carousel", "Carousel", "carousel"));
				widgetList.add(new FilterResult<String>("pagers", "Pagers", "pagers"));
				widgetList.add(new FilterResult<String>("formDisplay", "FormDisplay", "formDisplay"));
				widgetList.add(new FilterResult<String>("rollingPanel", "RollingPanel", "rollingPanel"));
				widgetList.add(new FilterResult<String>("storyBoard", "StoryBoard", "storyBoard"));
				widgetList.add(new FilterResult<String>("styledPanel", "StyledPanel", "styledPanel"));
				widgetList.add(new FilterResult<String>("confirm", "Confirm", "confirm"));
				widgetList.add(new FilterResult<String>("dialogBox", "DialogBox", "dialogBox"));
				widgetList.add(new FilterResult<String>("dialogViewContainer", "DialogViewContainer", "dialogViewContainer"));
				widgetList.add(new FilterResult<String>("messageBox", "MessageBox", "messageBox"));
				widgetList.add(new FilterResult<String>("progressBox", "ProgressBox", "progressBox"));
				widgetList.add(new FilterResult<String>("image", "Image", "image"));
				widgetList.add(new FilterResult<String>("promoBanner", "PromoBanner", "promoBanner"));
				widgetList.add(new FilterResult<String>("slideshow", "SlideShow", "slideshow"));
				widgetList.add(new FilterResult<String>("scrollBanner", "ScrollBanner", "scrollBanner"));
				widgetList.add(new FilterResult<String>("swapPanel", "SwapPanel", "swapPanel"));
				widgetList.add(new FilterResult<String>("tabViewContainer", "Tab View Container", "tabViewContainer"));
				widgetList.add(new FilterResult<String>("swapViewContainer", "SwapViewContainer", "swapViewContainer"));
				widgetList.add(new FilterResult<String>("simpleViewContainer", "Simple View Container", "simpleViewContainer"));
				widgetList.add(new FilterResult<String>("binding", "Binding", "binding"));
				widgetList.add(new FilterResult<String>("rest", "Rest Communication", "rest"));
				widgetList.add(new FilterResult<String>("objectcloner", "Object Cloner", "objectcloner"));
				widgetList.add(new FilterResult<String>("simpleDatabase", "Simple Database", "simpleDatabase"));
				widgetList.add(new FilterResult<String>("timer", "Timer", "timer"));

				List<FilterResult<String>> result = new ArrayList<FilterResult<String>>();

				for (FilterResult<String> filterResult : widgetList) 
				{
					if(filterResult.getLabel().toLowerCase().contains(query.toLowerCase()))
					{
						result.add(filterResult);
					}
				}

				return result;
			}

			@Override
			public void onSelectItem(String selectedItem)
			{
				widgets.menuDisposal().showView(selectedItem, null);
				filter.setText("");
			}

		});
	}

	@Expose
	public void showMenu()
	{
		widgets.menuDisposal().showMenu();
	}

	@Expose
	public void navigateToSite()
	{
		Window.open("http://www.cruxframework.org", "_self", null);
	}

	@Expose
	public void navigateToProject()
	{
		Window.open("https://github.com/CruxFramework", "_self", null);
	}

	private static class File
	{
		public File(String name, String path)
		{
			this.name = name;
			this.path = path;
		}
		
		private String name;
		private String path;
		
		public String getName()
		{
			return name;
		}
		public String getPath()
		{
			return path;
		}
	}
	
	@Expose
	public void viewSourceCode()
	{
		final String viewId = widgets.menuDisposal().getCurrentView().toLowerCase();
		
		mapFilesToRead.put(viewId, 3);

		final ArrayList<File> sourceFiles = new ArrayList<File>();
		
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand()
		{
			@Override
			public boolean execute()
			{
				if(mapFilesToRead.get(viewId) == 0)
				{
					mapFilesToRead.put(viewId, 3);
					
					if(sourceFiles.size() > 0)
					{
						showSourcesDialog(sourceFiles);
					}					
					
					return false;
				}
				return true;
			}
		}, 500);
		
		sourceFilesProxy.listViewFiles(viewId, new LoadingCallback<ArrayList<GitResponse>>()
		{
			@Override
			public void onComplete(ArrayList<GitResponse> viewFiles)
			{
				decreaseFilesToRead(viewId);
				if(viewFiles != null)
				{
					for (GitResponse gitResponse : viewFiles)
					{
						sourceFiles.add(new File(gitResponse.getName(), gitResponse.getPath()));
					}
				}
			}
			
			@Override
			public void onError(Exception e)
			{
				try
				{
					decreaseFilesToRead(viewId);
				} 
				finally
				{
					if(e.getMessage() != null 
						&& 
						!StringUtils.isEmpty(e.getMessage()) 
						&& (e.getMessage().contains("Not Found"))
					   )
					{
						super.closeWaitPopup();			
					}
					else
					{
						MessageBox.show(messages.maxCalls(), MessageType.ERROR);
						super.closeWaitPopup();
					}
				}
			}
		});
		
		sourceFilesProxy.listControllerFiles(viewId, new LoadingCallback<ArrayList<GitResponse>>()
		{
			@Override
			public void onComplete(ArrayList<GitResponse> controllerFiles)
			{
				decreaseFilesToRead(viewId);
				if(controllerFiles != null)
				{
					for (GitResponse gitResponse : controllerFiles)
					{
						sourceFiles.add(new File(gitResponse.getName(), gitResponse.getPath()));
					}
				}
			}
			
			@Override
			public void onError(Exception e)
			{
				try
				{
					decreaseFilesToRead(viewId);
				} 
				finally
				{
					super.closeWaitPopup();			
				}
			}
		});
		
		sourceFilesProxy.listServerFiles(viewId, new LoadingCallback<ArrayList<GitResponse>>()
		{
			@Override
			public void onComplete(ArrayList<GitResponse> serverFiles)
			{
				decreaseFilesToRead(viewId);
				if(serverFiles != null)
				{
					for (GitResponse gitResponse : serverFiles)
					{
						sourceFiles.add(new File(gitResponse.getName(), gitResponse.getPath()));
					}
				}
			}
			
			@Override
			public void onError(Exception e)
			{
				try
				{
					decreaseFilesToRead(viewId);
				} 
				finally
				{
					super.closeWaitPopup();			
				}
			}
		});
	}
	
	private void decreaseFilesToRead(final String viewId)
	{
		Integer filesToRead = mapFilesToRead.get(viewId);
		filesToRead--;
		mapFilesToRead.put(viewId, filesToRead);
	}

	/**
	 * Shows a dialog box with the source files contents
	 * @param files
	 */
	private void showSourcesDialog(final List<File> files)
	{
		DialogViewContainer dialog = DialogViewContainer.createDialog("sourcesPopup");
		dialog.openDialog();
		dialog.center();

		final PanelChoiceDisposal sourceChoice = dialog.getView().getWidget("sourceChoice", PanelChoiceDisposal.class);

		for (int i = files.size() - 1; i >= 0; i--)
		{
			final File file = files.get(i);
			addSourceChoice(sourceChoice, file.getPath(), file.getName());
		}

		if(files.size() > 0)
		{
			Scheduler.get().scheduleDeferred(new ScheduledCommand()
			{
				@Override
				public void execute()
				{
					String fileName = files.get(0).getName();
					sourceChoice.choose(fileName, fileName);
				}
			});
		}						
	}

	/**
	 * @param sourceChoice
	 * @param path
	 * @param fileName
	 */
	private void addSourceChoice(final PanelChoiceDisposal sourceChoice, final String path, final String fileName)
	{
		sourceChoice.addChoice(fileName, fileName, "sourceCode", new ViewActivateHandler()
		{
			private boolean loaded = false;

			@Override
			public void onActivate(ViewActivateEvent event)
			{
				if(!loaded)
				{
					loaded = true;

					RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, RAWTYPE_ENDPOINT + path);
					
					requestBuilder.setCallback(new RequestCallback()
					{
						@Override
						public void onResponseReceived(Request request, Response response)
						{
							if(response.getStatusCode() == 200)
							{
								String source = response.getText(); 
								View view = View.getView(fileName);
								Widget sourceEditor = view.getWidget("sourceEditor");
								Element editor = sourceEditor.getElement();
								String brush = "class=\"language-" + (fileName.endsWith("java") ? "java": "markup") + "\"";
								source = new SafeHtmlBuilder().appendEscaped(source).toSafeHtml().asString();
								editor.setInnerHTML("<pre class=\"line-numbers\"><code " + brush + ">" + source + "</code></pre>");
								syntaxHighlight();								
							}
							else
							{
								Crux.getErrorHandler().handleError("Error to get source file from GitHub");
							}
						}
						
						@Override
						public void onError(Request request, Throwable exception)
						{
							Crux.getErrorHandler().handleError(exception);
						}
					});
					
					try
					{
						requestBuilder.send();
					}
					catch (RequestException e)
					{
						Crux.getErrorHandler().handleError(e);
					}
				}
			}
		});
	}

	public native void syntaxHighlight()/*-{
		$wnd.Prism.highlightAll();
	}-*/;

	public void setMessages(ShowcaseCoreMessages messages)
	{
		this.messages = messages;
	}
	
	public void setSourceFilesProxy(SourceFilesProxy sourceFilesProxy)
	{
		this.sourceFilesProxy = sourceFilesProxy;
	}
}
