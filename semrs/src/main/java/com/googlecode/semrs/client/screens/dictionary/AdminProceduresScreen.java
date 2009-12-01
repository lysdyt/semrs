package com.googlecode.semrs.client.screens.dictionary;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.googlecode.semrs.client.ExtendedMessageBox;
import com.googlecode.semrs.client.MainPanel;
import com.googlecode.semrs.client.ShowcasePanel;
import com.gwtext.client.core.Connection;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.core.SortDir;
import com.gwtext.client.core.UrlParam;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.HttpProxy;
import com.gwtext.client.data.JsonReader;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.data.event.StoreListenerAdapter;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.MessageBoxConfig;
import com.gwtext.client.widgets.PagingToolbar;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.ToolTip;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.ToolbarTextItem;
import com.gwtext.client.widgets.WaitConfig;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.form.Field;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.Form;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.HtmlEditor;
import com.gwtext.client.widgets.form.NumberField;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.FieldListenerAdapter;
import com.gwtext.client.widgets.form.event.FormListenerAdapter;
import com.gwtext.client.widgets.form.event.FormPanelListenerAdapter;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.GridView;
import com.gwtext.client.widgets.grid.RowSelectionModel;
import com.gwtext.client.widgets.grid.event.GridCellListener;
import com.gwtext.client.widgets.layout.AnchorLayoutData;
import com.gwtext.client.widgets.layout.ColumnLayout;
import com.gwtext.client.widgets.layout.ColumnLayoutData;
import com.gwtext.client.widgets.layout.FitLayout;
import com.gwtext.client.widgets.layout.FormLayout;
import com.gwtextux.client.widgets.form.ItemSelector;

public class AdminProceduresScreen  extends ShowcasePanel {

	 private com.gwtext.client.widgets.TabPanel tabPanel;
	  
	    private FormPanel procedureForm = null;
	    
	    private com.gwtext.client.widgets.Window editProceduresWindow = null;
	     
	     public static boolean reloadFlag = false;
	     
	     private static String procedureId;
	     
	     //private static String deleteURL;
	     
	     FieldDef[] fieldDefs = new FieldDef[] { 
	     		new StringFieldDef("id"),
	            new StringFieldDef("name"), 
	            new StringFieldDef("description"), 
	     		new StringFieldDef("lastEditDate"),	
	     		new StringFieldDef("lastEditUser")
	     };

	     RecordDef recordDef = new RecordDef(fieldDefs);

	     JsonReader reader = new JsonReader("response.value.items", recordDef);

	     HttpProxy proxy = new HttpProxy("/semrs/procedureServlet", Connection.GET);
	     final Store store = new Store(proxy,reader,true);
	     final PagingToolbar pagingToolbar = new PagingToolbar(store);
	     
	     public AdminProceduresScreen(){
	    	//reader.setVersionProperty("response.value.version");
	         reader.setTotalProperty("response.value.total_count");
	         reader.setId("id");
	     }
	     
	     protected void onActivate() {
	    	 if(reloadFlag){
	    	   store.load(0, pagingToolbar.getPageSize()); 
	    	   reloadFlag = false;
	    	 }
	     }
	
	public Panel getViewPanel() {
  if (panel == null) {
      panel = new Panel();
      MainPanel.resetTimer();

      
      store.setDefaultSort("id", SortDir.ASC);
      store.addStoreListener(new StoreListenerAdapter() {
      	public void onLoadException(Throwable error) {
      		  //Check for session expiration
      		  RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, "/semrs/index.jsp");
      	        try {
      	            rb.sendRequest(null, new RequestCallback() {

      	                public void onError(Request request, Throwable exception) {
      	                	MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de procedimientos.");
      	                }
								public void onResponseReceived(Request arg0, Response arg1) {
									String errorMessage = arg1.getText();
				            		if(errorMessage.indexOf("login") != -1){
				            			MessageBox.alert("Error", "Su sesi&oacute;n de usuario ha expirado, presione OK para volver a loguearse." ,  
				                                 new MessageBox.AlertCallback() { 
													public void execute() {
														redirect("/semrs/");
													}  
				                                 });  
				            			
				            		}else{
				            			MessageBox.alert("Error", "Ha ocurrido un error al tratar de obtener la lista de procedimientos.");
				            		}
								}
      	            });
      	      } catch (RequestException e) {
  	        	MessageBox.alert("Error", "Ha ocurrido un error al tratar de conectarse con el servidor.");
  	        }
				}
      	 public void onDataChanged(Store store) {
      		 MainPanel.resetTimer();
      	    }

      });
    


             
      final FormPanel formPanel = new FormPanel();
      formPanel.setFrame(true);  
      formPanel.setTitle("B&uacute;squeda de Procedimientos");  
      formPanel.setWidth(900);  
      formPanel.setLabelWidth(100); 
      formPanel.setPaddings(5, 5, 5, 0);  
      formPanel.setLabelAlign(Position.TOP); 
      formPanel.setIconCls("procedure-icon");

      Panel topPanel = new Panel();  
      topPanel.setLayout(new ColumnLayout());  

      //create first panel and add fields to it  
      Panel columnOnePanel = new Panel();  
      columnOnePanel.setLayout(new FormLayout());  

      TextField id = new TextField("C&oacute;digo", "id");
      columnOnePanel.add(id, new AnchorLayoutData("65%"));  

      //add first panel as first column with 50% of the width  
      topPanel.add(columnOnePanel, new ColumnLayoutData(.5));  

      //create second panel and add fields to it  
      
      Panel columnTwoPanel = new Panel();  
      columnTwoPanel.setLayout(new FormLayout());  
     
      TextField labTestName = new TextField("Nombre", "name");  
      columnTwoPanel.add(labTestName, new AnchorLayoutData("65%")); 
      topPanel.add(columnTwoPanel, new ColumnLayoutData(0.5));  
      /*
      TextField drugDesc = new TextField("Descripci&oacute;n", "description");   
      columnTwoPanel.add(drugDesc, new AnchorLayoutData("65%"));  
      

      TextField lastName = new TextField("Apellido", "lastName");  
      columnTwoPanel.add(lastName, new AnchorLayoutData("65%"));  
      */
      //add the second panel as the second column to the top panel to take up the other 50% width  
      
      
      FieldSet fieldSet = new FieldSet();
      fieldSet.add(topPanel);
     
      
      Panel proxyPanel = new Panel();  
      proxyPanel.setBorder(true);  
      proxyPanel.setBodyBorder(false);
      proxyPanel.setCollapsible(false);  
      proxyPanel.setLayout(new FormLayout());
      proxyPanel.setButtonAlign(Position.CENTER);
      
      Button clear = new Button("Limpiar");
      clear.setIconCls("clear-icon");
      clear.addListener(new ButtonListenerAdapter(){
      	
      	public void onClick(Button button, EventObject e){
      		formPanel.getForm().reset();
      
      	}
      	
      });
      proxyPanel.addButton(clear);  
      
      final Button search = new Button("Buscar");  
      search.setIconCls("search-icon");  
      search.addListener(new ButtonListenerAdapter(){
      	
      	public void onClick(Button button, EventObject e){
      		UrlParam[] params = getFormData(formPanel.getForm());
      		store.setBaseParams(params);
      		store.load(0, pagingToolbar.getPageSize());
      		//store.removeAll();
      		//store.reload(params);
      		//store.commitChanges();
      		pagingToolbar.updateInfo();
      		MainPanel.resetTimer();
      	}
      	
      });
      proxyPanel.addButton(search);  
      fieldSet.add(proxyPanel);
      formPanel.add(fieldSet);  
     // formPanel.add(proxyPanel);
      
      formPanel.setMonitorValid(true);
      formPanel.addListener(new FormPanelListenerAdapter() {
          public void onClientValidation(FormPanel formPanel, boolean valid) {
          	search.setDisabled(!valid);
          }
      });

      
      
      
      
     
      GridView view = new GridView();
      view.setEmptyText("No hay Registros");
      view.setAutoFill(true);
      view.setForceFit(true);

      GridPanel grid = new GridPanel(store, createColModel(true, true));
      grid.setEnableDragDrop(false);
      grid.setWidth(850);
      grid.setHeight(420);
      grid.setTitle("Lista de Procedimientos");
      grid.setLoadMask(true);  
      grid.setSelectionModel(new RowSelectionModel());  
      grid.setFrame(true);  
      grid.setView(view);
      grid.addGridCellListener(new GridCellListener() {  

				
				public void onCellDblClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
				    Record r = grid.getStore().getAt(rowIndex);
				    String recordId = r.getAsString("id");
				    setProcedureId(recordId);
				    //procedureId = recordId;
				    com.gwtext.client.widgets.Window procedureWindow = getEditProcedureWindow(false);
				    procedureWindow.show();
				    //getEditGroupWindow(procedureId).show();
				}

				
				public void onCellClick(GridPanel grid, int rowIndex,
						int colIndex, EventObject e) {
					final Record r = grid.getStore().getAt(rowIndex);
					String recordId = r.getAsString("id");
					
				}

	
				public void onCellContextMenu(GridPanel grid, int rowIndex,
						int cellIndex, EventObject e) {
					// TODO Auto-generated method stub
					
				}
          });  
      
     
      pagingToolbar.setPageSize(20);
      pagingToolbar.setDisplayInfo(true);
      pagingToolbar.setEmptyMsg("No hay registros");


      NumberField pageSizeField = new NumberField();
      pageSizeField.setWidth(40);
      pageSizeField.setSelectOnFocus(true);
      pageSizeField.addListener(new FieldListenerAdapter() {
          public void onSpecialKey(Field field, EventObject e) {
              if (e.getKey() == EventObject.ENTER) {
                  int pageSize = Integer.parseInt(field.getValueAsString());
                  pagingToolbar.setPageSize(pageSize);
              }
          }
      });

      ToolTip toolTip = new ToolTip("Introduzca el tama&ntilde;o de p&aacute;gina");
      toolTip.applyTo(pageSizeField);
       
      pagingToolbar.addField(pageSizeField);
      pagingToolbar.addSeparator();
      ToolbarButton newLabTestWindow = new ToolbarButton("Nuevo Procedimiento", new ButtonListenerAdapter() {  
      	public void onClick(Button button, EventObject e) {
      	   setProcedureId("");
      	   //procedureId = "";
      	    com.gwtext.client.widgets.Window drugWindow = getEditProcedureWindow(true);
				    drugWindow.show();
				// getEditGroupWindow("").show();
      	}  
      });  
      newLabTestWindow.setIconCls("add-icon");
      pagingToolbar.addButton(newLabTestWindow);
      pagingToolbar.addSeparator();
      
      //pagingToolbar.addButton(deleteGroupButton);
      //pagingToolbar.addSeparator();
      ToolbarButton exportButton = new ToolbarButton("Exportar", new ButtonListenerAdapter() {  
      	public void onClick(Button button, EventObject e) {
      		Window.open("/semrs/procedureServlet?export=true", "_self", ""); 

      	}  
      });  
      exportButton.setIconCls("excel-icon");
      pagingToolbar.addButton(exportButton);
      pagingToolbar.addSeparator();
      pagingToolbar.setDisplayMsg("Mostrando Registros {0} - {1} de {2}");
      grid.setBottomToolbar(pagingToolbar);
      
      
      grid.addListener(new PanelListenerAdapter() {  
                   public void onRender(Component component) {  
                  	 store.load(0, pagingToolbar.getPageSize()); 
                   }  
               }); 
      formPanel.add(grid, new AnchorLayoutData("100%"));
      
      panel.add(formPanel);
      
  }


  return panel;
}




	public com.gwtext.client.widgets.TabPanel getTabPanel() {
		return tabPanel;
	}

	public void setTabPanel(com.gwtext.client.widgets.TabPanel tabPanel) {
		this.tabPanel = tabPanel;
	}
	
	
	public com.gwtext.client.widgets.Window getEditProcedureWindow(boolean isNew){
		  
		 if(editProceduresWindow!=null){
			editProceduresWindow.clear();
		 }
		
		  editProceduresWindow = new com.gwtext.client.widgets.Window();  
		  editProceduresWindow.setTitle("Editar Procedimiento");  
		  editProceduresWindow.setWidth(700);  
		  editProceduresWindow.setHeight(600);    
		  editProceduresWindow.setLayout(new FitLayout());  
		  editProceduresWindow.setPaddings(5);  
		  editProceduresWindow.setResizable(true);
		  editProceduresWindow.setButtonAlign(Position.CENTER);  
		  editProceduresWindow.setModal(true);
		  editProceduresWindow.setId("editProceduresWindow");
		  editProceduresWindow.setIconCls("procedure-icon");
		  editProceduresWindow.setCloseAction(com.gwtext.client.widgets.Window.HIDE);  
		  editProceduresWindow.setMaximizable(true);
		  //editProceduresWindow.setMinimizable(true);
		  
		   RecordDef recordDef = new RecordDef(new FieldDef[]{   
	        		new StringFieldDef("id"),  
	        		new StringFieldDef("name"),  
	        		new StringFieldDef("description")
	        		});  

	        final JsonReader reader = new JsonReader("data", recordDef);  
	        reader.setSuccessProperty("success"); 
	        reader.setId("id");

	        //setup error reader to process from submit response from server  
	        RecordDef errorRecordDef = new RecordDef(new FieldDef[]{  
	        		new StringFieldDef("id"),  
	        		new StringFieldDef("msg")  
	        });  

	        final JsonReader errorReader = new JsonReader("field", errorRecordDef);  
	        errorReader.setSuccessProperty("success"); 
	        
	      if(procedureForm!=null){
	    	  procedureForm.clear();
	      }

	        procedureForm = new FormPanel(); 
	        
	        procedureForm.setReader(reader);  
	        procedureForm.setErrorReader(errorReader); 
	        procedureForm.setFrame(true);  
	        procedureForm.setWidth(700);  
	        procedureForm.setHeight(600);
	        procedureForm.setAutoScroll(true);
	        procedureForm.setId("editProcedureForm");
	        
			Panel proxyPanel = new Panel();  
			proxyPanel.setBorder(true);  
			proxyPanel.setBodyBorder(false);
			proxyPanel.setCollapsible(false);  
			proxyPanel.setLayout(new FormLayout());
			proxyPanel.setButtonAlign(Position.CENTER);
			//proxyPanel.setIconCls("groupProxyPanel");

	        FieldSet procedureFS = new FieldSet("Informaci&oacute;n de Procedimiento");  
	        procedureFS.setCollapsible(true);
	        procedureFS.setFrame(false);  
	        procedureFS.setId("procedureFS");
	        
	        TextField procedureIdText = new TextField("C&oacute;digo","id",190);
	        procedureIdText.setId("procedureIdText");
	        if(!getProcedureId().equals("")){
	          procedureIdText.setReadOnly(true);
	        }
	        procedureIdText.setAllowBlank(false);
	        procedureIdText.setStyle("textTransform: uppercase;");
	        procedureIdText.addListener(new FieldListenerAdapter(){
	        	 public void onBlur(Field field) {
	            String value = field.getValueAsString();
	            field.setValue(value.toUpperCase());
	        	}
	        	
	        });
	        procedureFS.add(procedureIdText);  
	        
	        TextField loadSuccess = new TextField("loadSuccess","loadSuccess",190);
	        loadSuccess.setId("procedureLoadSuccess");
	        loadSuccess.setVisible(false);
	        procedureFS.add(loadSuccess);
	        
	        TextField procedureNameText = new TextField("Nombre", "name", 190);  
	        procedureNameText.setId("procedureNameText");
	        procedureNameText.setAllowBlank(false);
	        procedureFS.add(procedureNameText); 
	        
	        
	        HtmlEditor procedureDescText = new HtmlEditor("Descripci&oacute;n", "description");  
	        procedureDescText.setId("procedureDesc");
	       // drugDesc.setWidth(190);
	        procedureDescText.setHeight(200);  
	        procedureFS.add(procedureDescText); 
	        
	        procedureForm.add(procedureFS);
	        
	        FieldSet procedureDiseasesFS = new FieldSet("Enfermedades");  
	        procedureDiseasesFS.setId("procedureDiseasesFS");
	        procedureDiseasesFS.setCollapsible(true);
	        procedureDiseasesFS.setFrame(false);  
	        
	        FieldDef[] gridFieldDefs = new FieldDef[] { 
		     		new StringFieldDef("id"),
		            new StringFieldDef("name")
		     };

		    RecordDef gridRecordDef = new RecordDef(gridFieldDefs);
		    JsonReader gridReader = new JsonReader("response.value.items", gridRecordDef);
		    HttpProxy gridProxy = new HttpProxy("/semrs/procedureServlet?procedureEdit=getDiseases&procedureId="+getProcedureId()+"&procedureDiseases=true", Connection.GET);
		    final Store gridStore = new Store(gridProxy,gridReader,true);
		    gridStore.load();
		    
			FieldDef[] gridFieldDefs2 = new FieldDef[] { 
      		     	new StringFieldDef("id"),
      		        new StringFieldDef("name")
      		};

      		RecordDef gridRecordDef2 = new RecordDef(gridFieldDefs2);
      		JsonReader gridReader2 = new JsonReader("response.value.items", gridRecordDef2);
      		HttpProxy gridProxy2 = new HttpProxy("/semrs/procedureServlet?procedureEdit=getDiseases&procedureId="+getProcedureId(), Connection.GET);
      		final Store innerGridStore = new Store(gridProxy2,gridReader2,true);
			
      		procedureDiseasesFS.add(getGrid(gridStore,innerGridStore ,createColModel(false,false), "Enfermedades Relacionadas", "disease-icon"), new AnchorLayoutData("100%"));

			
			procedureForm.add(procedureDiseasesFS);
	        
			 final Button saveButton = new Button("Guardar");
			 saveButton.setId("procedureSaveButton");
		        saveButton.addListener(new ButtonListenerAdapter(){
		        	public void onClick(final Button button, EventObject e){
		        		
		        		MessageBox.show(new MessageBoxConfig() {  
		        			{  
		        				setMsg("Guardando los cambios, por favor espere...");  
		        				setProgressText("Guardando...");  
		        				setWidth(300);  
		        				setWait(true);  
		        				setWaitConfig(new WaitConfig() {  
		        					{  
		        						setInterval(200);  
		        					}  
		        				});  
		        				setAnimEl(button.getId());  
		        			}  
		        		});  
		        		
		        		 
		        		 try {
		        			 RequestBuilder saveRequest = new RequestBuilder(RequestBuilder.POST, "/semrs/procedureServlet?procedureEdit=submit&isNew="+getProcedureId()+"&"+procedureForm.getForm().getValues()+"&procedureDiseases="+getRecordValues(gridStore.getRecords()) );
		        			 saveRequest.sendRequest(null, new RequestCallback() {
		                        public void onResponseReceived(Request req, Response res) {
		                           MessageBox.hide();  
				                   MessageBox.getDialog().close();
		                           if(res.getText().indexOf("errores") !=-1){
		                        	   MessageBox.hide();  
		                        	   MessageBox.alert("Error", res.getText()); 
		                           }else if(res.getText().equals("")){
		                        	   MessageBox.hide();
			                           MessageBox.alert("Error", "Error interno"); 
		                           }else{
		                           MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText());
		                           MainPanel.resetTimer();
		                           editProceduresWindow.hide();
		                           store.reload();
		                           }
		                        }
		                        
		                        public void onError(Request req, Throwable exception) {
		                           MessageBox.hide();  
				                   MessageBox.getDialog().close();
		                           MessageBox.alert("Error", "Error interno"); 
		                        }
		                        
		                     });
		                     
		                  } catch (RequestException re) {
		                	 MessageBox.hide();  
		                     MessageBox.getDialog().close();
		                	 MessageBox.alert("Error", "Error interno"); 
		                  }
		        	}
		        	
		        });
		        saveButton.setIconCls("save-icon");
		        proxyPanel.addButton(saveButton);
		        
		        
		        Button delete = new Button("Eliminar");
		        delete.setId("procedureDeleteButton");
		        if(getProcedureId().equals("")){
		        	delete.setDisabled(true);
		        }else{
		        	delete.setDisabled(false);
		        }
		        final RequestBuilder rb = new RequestBuilder(RequestBuilder.POST, "/semrs/procedureServlet?procedureEdit=delete&id="+getProcedureId());
		        delete.addListener(new ButtonListenerAdapter(){
		        	public void onClick(final Button button, EventObject e){
		        		
		        		ExtendedMessageBox.confirmlg("Confirmar","Esta seguro que desea eliminar este Procedimiento?", "Si", "No",  
		                           new MessageBox.ConfirmCallback() {  
		                               public void execute(String btnID) {
		                              	 if(btnID.equals("yes")){
		                              		MessageBox.show(new MessageBoxConfig() {  
		            	    					{  
		            	    						setMsg("Eliminando registro, por favor espere...");  
		            	    						setProgressText("Eliminando...");  
		            	    						setWidth(300);  
		            	    						setWait(true);  
		            	    						setWaitConfig(new WaitConfig() {  
		            	    							{  
		            	    								setInterval(200);  
		            	    							}  
		            	    						});  
		            	    						setAnimEl(button.getId());  
		            	    					}  
		            	    				});  


		            	    				try {
		            	    					
		            	    					rb.sendRequest(null, new RequestCallback() {
		            	    						public void onResponseReceived(Request req, Response res) {
		            	    							MessageBox.hide();  
		            				                    MessageBox.getDialog().close(); 
		            	    							if(res.getText().indexOf("errores") !=-1){
		            	    								MessageBox.alert("Error", res.getText()); 
		            	    							 }else if(res.getText().equals("")){
		            			                        	   MessageBox.hide();
		            				                           MessageBox.alert("Error", "Error interno"); 
		            			                           }else{
		            	    								MainPanel.resetTimer();
		            	    								MessageBox.alert("Operaci&oacute;n Ex&iacute;tosa", res.getText() ,  
		            	    		                                 new MessageBox.AlertCallback() { 
		            	    									public void execute() {
		            	    										editProceduresWindow.hide();
		            	    										store.reload();
		            	    									}  
		            	                                     });  
		            	    								
		            	    							}


		            	    						}

		            	    						public void onError(Request req, Throwable exception) {
		            	    							MessageBox.hide();  
		            				                    MessageBox.getDialog().close();
		            	    							MessageBox.alert("Error", "Error interno"); 
		            	    						}

		            	    					});

		            	    				} catch (RequestException re) {
		            	    					MessageBox.hide();  
		      			                        MessageBox.getDialog().close();
		            	    					MessageBox.alert("Error", "Error interno"); 
		            	    				}
		                              	 }
		                               }  
		                           });  
		        		
		        	    }
		        	
		        });
		        delete.setIconCls("delete-icon");
		        proxyPanel.addButton(delete);  
		        
		        
		        Button cancel = new Button("Cancelar");
		        cancel.setId("cancelProcedureButton");
		        cancel.addListener(new ButtonListenerAdapter(){
		        	public void onClick(Button button, EventObject e){
		        		editProceduresWindow.hide();
		        	}
		        	
		        });
		        cancel.setIconCls("cancel-icon");
		        proxyPanel.addButton(cancel);  
			
		        procedureForm.add(proxyPanel);
			
	        procedureForm.setMonitorValid(true);
	        procedureForm.addListener(new FormPanelListenerAdapter() {
	            public void onClientValidation(FormPanel formPanel, boolean valid) {
	            	saveButton.setDisabled(!valid);
	            }
	        });
	      procedureForm.doLayout();
	      
	      if(!isNew){
	    	  procedureForm.getForm().load("/semrs/procedureServlet?procedureEdit=load&id="+getProcedureId(), null, Connection.GET, "Cargando...");
	    	  procedureForm.getForm().addListener(new FormListenerAdapter(){
		    	   public void onActionComplete(Form form, int httpStatus, String responseText) {
		    		  if(form.findField("procedureLoadSuccess").getValueAsString().equals("false")){
		    				MessageBox.show(new MessageBoxConfig() {  
		    					{  
		    						setTitle("Error");
		    						setMsg("Este procedimiento no existe");
		    						setIconCls(MessageBox.ERROR);
		    					    setModal(true);
		    					    setButtons(MessageBox.OK);
		    					    setCallback(new MessageBox.PromptCallback() { 
		    						public void execute(
		    								String btnID,
		    								String text) {
		    							store.reload();
		    							editProceduresWindow.close();
		    							
		    						}  
		                            });  
		    					}  
		    				});
		    			  
		    		  }    
		    	   }
		    	   public void onActionFailed(Form form, int httpStatus, String responseText) {
		   			MessageBox.show(new MessageBoxConfig() {  
						{  
							setTitle("Error");
							setMsg("Ocurrio un error al tratar de obtener este procedimiento");
							setIconCls(MessageBox.ERROR);
						    setModal(true);
						    setButtons(MessageBox.OK);
						    setCallback(new MessageBox.PromptCallback() { 
							public void execute(
									String btnID,
									String text) {
								store.reload();
								editProceduresWindow.close();
								
							}  
	                        });  
						}  
					});
		    	    }
		    	   
		       });
		      }
	      editProceduresWindow.add(procedureForm);  
	      editProceduresWindow.doLayout();
		  return editProceduresWindow;
	}

	public String getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(String procedureId) {
		this.procedureId = procedureId;
	}


}
