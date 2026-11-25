package pearlmart.ui;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import pearlmart.core.*;
import java.math.BigDecimal;
import java.util.function.Function;

public final class Workbench extends Application {
    private PearlMartFacade app; private String saleId; private final BorderPane root=new BorderPane();
    private final Label feedback=new Label("Ready"), metrics=new Label();
    private final TableView<InventoryItem> inventory=new TableView<>(); private final TableView<SaleLine> basket=new TableView<>();
    private final TextArea receipt=new TextArea(); private final Label subtotal=new Label(), discount=new Label(), tax=new Label(), payable=new Label();
    private final TextField saleKey=field("Item ID or barcode"),saleQty=field("Quantity");
    private Button addLineButton,removeButton,confirmButton,cancelButton;
    @Override public void start(Stage stage) throws Exception {
        app=new PearlMartFacade(new RatePolicy(new BigDecimal("100"),new BigDecimal("0.05"),BigDecimal.ZERO)); DemoData.load(app);
        Label logo=new Label("PEARL / MART");logo.getStyleClass().add("logo");Label subtitle=new Label("Inventory workbench\nTEAM B54 · G04");subtitle.getStyleClass().add("nav-caption");
        Button stock=button("01   Stock desk",()->showStock()), sale=button("02   Checkout",()->showSale()), logs=button("03   Activity log",()->showActivity());
        VBox nav=new VBox(16,logo,subtitle,stock,sale,logs);nav.getStyleClass().add("nav");nav.setPrefWidth(220);for(Button b:new Button[]{stock,sale,logs}){b.getStyleClass().add("nav-button");b.setMaxWidth(Double.MAX_VALUE);}
        root.setLeft(nav);VBox bottom=new VBox(4,metrics,feedback);bottom.getStyleClass().add("status");root.setBottom(bottom);feedback.setWrapText(true);
        inventory.getColumns().add(col("ID",InventoryItem::id,90));inventory.getColumns().add(col("Barcode",InventoryItem::barcode,115));inventory.getColumns().add(col("Item",InventoryItem::name,150));inventory.getColumns().add(col("QAR",i->i.price().toPlainString(),80));inventory.getColumns().add(col("On hand",i->Integer.toString(i.quantity()),85));
        basket.getColumns().add(col("Item",SaleLine::name,155));basket.getColumns().add(col("Qty",l->Integer.toString(l.quantity()),55));basket.getColumns().add(col("Unit QAR",l->l.unitPrice().toPlainString(),85));basket.getColumns().add(col("Amount",l->l.amount().toPlainString(),90));
        receipt.setEditable(false);receipt.setPrefRowCount(10);receipt.setWrapText(false);showStock();Scene scene=new Scene(root,1250,760);scene.getStylesheets().add(getClass().getResource("/workbench.css").toExternalForm());stage.setTitle("PearlMart | Inventory Workbench");stage.setMinWidth(1050);stage.setMinHeight(670);stage.setScene(scene);stage.show();
    }
    private static TextField field(String hint){TextField f=new TextField();f.setPromptText(hint);return f;}
    private static <T> TableColumn<T,String> col(String title,Function<T,String> f,double width){TableColumn<T,String> c=new TableColumn<>(title);c.setCellValueFactory(v->new ReadOnlyStringWrapper(f.apply(v.getValue())));c.setPrefWidth(width);return c;}
    private Button button(String name,Runnable action){Button b=new Button(name);b.setOnAction(e->run(action));return b;}
    private void run(Runnable r){try{r.run();}catch(RuntimeException e){message(e.getMessage()==null?e.toString():e.getMessage(),true);}finally{refresh();}}
    private void message(String s,boolean error){feedback.setText((error?"ERROR  |  ":"OK  |  ")+s);feedback.setStyle(error?"-fx-text-fill: #a22b26;":"-fx-text-fill: #18584b;");}
    private int qty(TextField f){try{return Integer.parseInt(f.getText().strip());}catch(NumberFormatException e){throw new IllegalArgumentException("Enter a whole-number quantity");}}
    private BigDecimal price(TextField f){try{return new BigDecimal(f.getText().strip());}catch(NumberFormatException e){throw new IllegalArgumentException("Enter a valid price");}}
    private VBox page(String title,String description,Node... nodes){Label h=new Label(title);h.getStyleClass().add("heading");Label sub=new Label(description);sub.getStyleClass().add("muted");VBox p=new VBox(12,h,sub);p.getChildren().addAll(nodes);p.getStyleClass().add("page");return p;}
    private VBox card(String title,Node... nodes){Label l=new Label(title);l.getStyleClass().add("section-title");VBox c=new VBox(9,l);c.getChildren().addAll(nodes);c.getStyleClass().add("panel");return c;}
    private void showStock(){
        TextField key=field("ID or barcode"), amount=field("Units to add"), id=field("Unique item ID"), barcode=field("Unique barcode"), name=field("Item name"), cost=field("Selling price (QAR)"), manufacturer=field("Manufacturer"), origin=field("Place of origin"), comments=field("Comments (optional)"), newQty=field("Opening quantity");DatePicker expiry=new DatePicker();expiry.setPromptText("Expiry date (optional)");Label found=new Label("Look up an existing item or register a new one.");found.setWrapText(true);
        Button lookup=button("Find item",()->{var i=app.find(key.getText()).orElseThrow(()->new IllegalArgumentException("Not found. Use Register new item."));found.setText(i.name()+" · "+i.quantity()+" units · QAR "+i.price());message("Item located",false);});
        Button restock=button("Confirm stock addition",()->{var i=app.restock(key.getText(),qty(amount));found.setText(i.name()+" · "+i.quantity()+" units");message("Stock movement recorded",false);});restock.getStyleClass().add("primary");
        Button register=button("Register new item",()->{var i=app.register(new InventoryItem(id.getText(),barcode.getText(),name.getText(),price(cost),qty(newQty),manufacturer.getText(),origin.getText(),expiry.getValue(),comments.getText()));message(i.name()+" registered with "+i.quantity()+" units",false);for(TextField f:new TextField[]{id,barcode,name,cost,manufacturer,origin,comments,newQty})f.clear();expiry.setValue(null);});register.getStyleClass().add("primary");
        GridPane fields=new GridPane();fields.setHgap(8);fields.setVgap(8);fields.addRow(0,id,barcode);fields.addRow(1,name,cost);fields.addRow(2,manufacturer,origin);fields.addRow(3,newQty,expiry);fields.add(comments,0,4,2,1);
        VBox form=new VBox(12,card("Restock an existing item",new HBox(8,key,lookup),found,amount,restock),card("Register a new item",fields,register));form.setPrefWidth(470);
        VBox catalog=card("Live inventory",inventory);HBox.setHgrow(catalog,Priority.ALWAYS);VBox.setVgrow(inventory,Priority.ALWAYS);HBox row=new HBox(14,form,catalog);VBox p=page("Stock desk","UC04  /  Receive and register stock additions",row);VBox.setVgrow(row,Priority.ALWAYS);root.setCenter(p);refresh();
    }
    private void showSale(){
        if(saleId==null){saleId=app.start().id();receipt.clear();}saleKey.clear();saleQty.clear();
        Button start=button("New transaction",()->{if(app.sale(saleId).status()==Sale.Status.PENDING)throw new IllegalStateException("Confirm or cancel the pending sale first");saleId=app.start().id();receipt.clear();message("New sale opened",false);});
        addLineButton=button("Add to basket",()->{app.addLine(saleId,saleKey.getText(),qty(saleQty));saleKey.clear();saleQty.clear();message("Line validated",false);});
        removeButton=button("Remove selected",()->{var l=basket.getSelectionModel().getSelectedItem();if(l==null)throw new IllegalArgumentException("Select a basket line");app.removeLine(saleId,l.itemId());message("Line removed",false);});
        confirmButton=button("Confirm & issue receipt",()->{SalesReceipt r=app.confirm(saleId);receipt.setText(format(r));message("Sale completed; stock deducted once",false);});confirmButton.getStyleClass().add("primary");
        cancelButton=button("Cancel pending sale",()->{Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Cancel this pending sale? Stock will remain unchanged.",ButtonType.YES,ButtonType.NO);if(a.showAndWait().orElse(ButtonType.NO)==ButtonType.YES){app.cancel(saleId);receipt.setText("CANCELLED\n"+saleId+"\nNo completed receipt issued.");message("Cancellation recorded; stock unchanged",false);}else message("Pending sale retained",false);});cancelButton.getStyleClass().add("danger");
        VBox left=card("Purchase basket",new HBox(8,saleKey,saleQty,addLineButton),basket,removeButton,start);VBox.setVgrow(basket,Priority.ALWAYS);HBox.setHgrow(left,Priority.ALWAYS);VBox right=card("Payable summary",subtotal,discount,tax,payable,confirmButton,cancelButton,new Label("Receipt / transaction result"),receipt);right.setPrefWidth(370);VBox.setVgrow(receipt,Priority.ALWAYS);payable.getStyleClass().add("total");HBox row=new HBox(14,left,right);VBox p=page("Checkout","UC13  /  Validate stock, price and confirm or cancel",row);VBox.setVgrow(row,Priority.ALWAYS);root.setCenter(p);refresh();
    }
    private String format(SalesReceipt r){StringBuilder b=new StringBuilder("PEARLMART  |  SALES RECEIPT\n"+r.saleId()+"\n"+r.time()+"\n------------------------------\n");for(SaleLine l:r.lines())b.append(l.name()).append("  x").append(l.quantity()).append(" @ ").append(l.unitPrice()).append(" = ").append(l.amount()).append('\n');Totals t=r.totals();return b.append("\nSubtotal: ").append(t.subtotal()).append("\nDiscount: ").append(t.discount()).append("\nTax: ").append(t.tax()).append("\nTOTAL QAR: ").append(t.payable()).toString();}
    private void showActivity(){TableView<InventoryMovement> m=new TableView<>();m.getColumns().add(col("Item",InventoryMovement::itemId,110));m.getColumns().add(col("Delta",v->Integer.toString(v.delta()),85));m.getColumns().add(col("Balance",v->Integer.toString(v.balance()),90));m.getColumns().add(col("Reference",InventoryMovement::reference,290));m.getColumns().add(col("Recorded",v->v.time().toString(),230));m.setItems(FXCollections.observableArrayList(app.movements()));TableView<Sale> s=new TableView<>();s.getColumns().add(col("Transaction",Sale::id,310));s.getColumns().add(col("Status",v->v.status().name(),130));s.getColumns().add(col("Lines",v->Integer.toString(v.lines().size()),80));s.setItems(FXCollections.observableArrayList(app.sales()));VBox p=page("Activity log","Session records / movement history and sale status",card("Inventory movements",m),card("Sales journal",s));root.setCenter(p);refresh();}
    private void refresh(){if(app==null)return;inventory.setItems(FXCollections.observableArrayList(app.items()));metrics.setText(app.items().size()+" ITEMS     •     "+app.items().stream().mapToLong(InventoryItem::quantity).sum()+" UNITS     •     "+app.movements().size()+" MOVEMENTS     |     Session storage");if(saleId==null)return;Sale s=app.sale(saleId);basket.setItems(FXCollections.observableArrayList(s.lines()));boolean pending=s.status()==Sale.Status.PENDING;if(addLineButton!=null){addLineButton.setDisable(!pending);removeButton.setDisable(!pending);confirmButton.setDisable(!pending||s.lines().isEmpty());cancelButton.setDisable(!pending);}Totals t=s.receipt()!=null?s.receipt().totals():null;if(pending&&!s.lines().isEmpty()){try{t=app.quote(saleId);}catch(RuntimeException e){message(e.getMessage(),true);confirmButton.setDisable(true);}}subtotal.setText("Subtotal   QAR "+(t==null?"—":t.subtotal()));discount.setText("Discount   QAR "+(t==null?"—":t.discount()));tax.setText("Tax   QAR "+(t==null?"—":t.tax()));payable.setText("Total   QAR "+(t==null?"—":t.payable()));}
    public static void main(String[] args){launch(args);}
}
