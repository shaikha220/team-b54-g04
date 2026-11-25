package pearlmart.core;
import java.math.BigDecimal;import java.util.*;
public final class WorkflowChecks {
    private static int count=0;
    private static PearlMartFacade fresh(){return new PearlMartFacade(new RatePolicy(new BigDecimal("100"),new BigDecimal("0.05"),BigDecimal.ZERO));}
    private static InventoryItem item(String id,String bar,int q){return new InventoryItem(id,bar,"Coffee",new BigDecimal("25"),q,"Roaster","Qatar",null,"");}
    private static void eq(Object a,Object b){if(!Objects.equals(a,b))throw new AssertionError("Expected "+b+" but got "+a);}
    private static void rejects(Runnable r){try{r.run();}catch(IllegalArgumentException|IllegalStateException|ArithmeticException e){return;}throw new AssertionError("Expected rejection");}
    private static void test(String id,Runnable r){try{r.run();System.out.println(id+" | PASS");count++;}catch(Throwable e){System.out.println(id+" | FAIL | "+e);throw e;}}
    public static void main(String[] args){
        test("A01 new item",()->{var a=fresh();a.register(item("A","B",10));eq(a.find("B").orElseThrow().quantity(),10);eq(a.movements().size(),1);});
        test("A02 existing barcode",()->{var a=fresh();a.register(item("A","B",10));a.restock("B",3);eq(a.find("A").orElseThrow().quantity(),13);eq(a.movements().get(1).delta(),3);});
        test("A03 duplicate ID/barcode",()->{var a=fresh();a.register(item("A","B",10));rejects(()->a.register(item("C","B",5)));rejects(()->a.register(item("A","D",5)));eq(a.items().size(),1);eq(a.movements().size(),1);});
        test("A04 zero/negative quantity",()->{var a=fresh();a.register(item("A","B",10));rejects(()->a.restock("A",0));rejects(()->a.restock("A",-1));rejects(()->a.register(item("C","D",0)));eq(a.find("A").orElseThrow().quantity(),10);eq(a.movements().size(),1);});
        test("A05 mandatory fields/price",()->{var a=fresh();rejects(()->a.register(new InventoryItem("A","B","",BigDecimal.ONE,2,"X","Q",null,"")));rejects(()->a.register(new InventoryItem("A","B","X",BigDecimal.ZERO,2,"X","Q",null,"")));eq(a.items().size(),0);});
        test("A06 overflow rollback",()->{var a=fresh();a.register(item("A","B",Integer.MAX_VALUE));rejects(()->a.restock("A",1));eq(a.find("A").orElseThrow().quantity(),Integer.MAX_VALUE);eq(a.movements().size(),1);});
        test("S01 multi-line confirm",()->{var a=fresh();a.register(item("A","B",10));a.register(item("C","D",6));String id=a.start().id();a.addLine(id,"B",2);a.addLine(id,"C",2);eq(a.quote(id).payable(),new BigDecimal("95.00"));eq(a.find("A").orElseThrow().quantity(),10);var r=a.confirm(id);eq(r.lines().size(),2);eq(r.totals().discount(),new BigDecimal("5.00"));eq(a.find("A").orElseThrow().quantity(),8);eq(a.find("C").orElseThrow().quantity(),4);eq(a.movements().size(),4);eq(a.sale(id).status(),Sale.Status.COMPLETED);rejects(()->a.confirm(id));eq(a.movements().size(),4);});
        test("S02 unknown item",()->{var a=fresh();String id=a.start().id();rejects(()->a.addLine(id,"X",1));eq(a.sale(id).lines().size(),0);eq(a.movements().size(),0);});
        test("S03 cumulative stock / invalid qty",()->{var a=fresh();a.register(item("A","B",3));String id=a.start().id();a.addLine(id,"A",2);rejects(()->a.addLine(id,"B",2));rejects(()->a.addLine(id,"A",0));eq(a.sale(id).lines().getFirst().quantity(),2);eq(a.find("A").orElseThrow().quantity(),3);});
        test("S04 pending cancellation",()->{var a=fresh();a.register(item("A","B",5));String id=a.start().id();a.addLine(id,"A",3);a.cancel(id);eq(a.sale(id).status(),Sale.Status.CANCELLED);eq(a.sale(id).receipt(),null);eq(a.find("A").orElseThrow().quantity(),5);eq(a.movements().size(),1);rejects(()->a.confirm(id));});
        test("S05 stock changes before confirm",()->{var a=fresh();a.register(item("A","B",5));a.register(item("C","D",2));String x=a.start().id();a.addLine(x,"A",1);a.addLine(x,"C",2);String y=a.start().id();a.addLine(y,"C",2);a.confirm(y);int n=a.movements().size();rejects(()->a.confirm(x));eq(a.find("A").orElseThrow().quantity(),5);eq(a.sale(x).status(),Sale.Status.PENDING);eq(a.movements().size(),n);});
        test("S06 empty / policy failure",()->{var a=new PearlMartFacade(lines->{throw new IllegalArgumentException("Invalid policy");});a.register(item("A","B",5));String id=a.start().id();rejects(()->a.confirm(id));a.addLine(id,"A",1);rejects(()->a.confirm(id));eq(a.find("A").orElseThrow().quantity(),5);eq(a.movements().size(),1);var b=fresh();rejects(()->b.quote(b.start().id()));});
        test("S07 shared inventory / no adjustments",()->{var a=fresh();a.register(item("A","B",1));a.restock("A",2);String id=a.start().id();a.addLine(id,"B",3);eq(a.quote(id).payable(),new BigDecimal("75.00"));a.confirm(id);eq(a.find("A").orElseThrow().quantity(),0);rejects(()->a.cancel(id));});
        test("S08 strategy alternate tax",()->{var a=new PearlMartFacade(new RatePolicy(BigDecimal.ZERO,new BigDecimal("0.10"),new BigDecimal("0.05")));a.register(item("A","B",2));String id=a.start().id();a.addLine(id,"A",2);eq(a.quote(id).payable(),new BigDecimal("47.25"));});
        test("S09 remove line",()->{var a=fresh();a.register(item("A","B",2));String id=a.start().id();a.addLine(id,"B",2);a.removeLine(id,"A");eq(a.sale(id).lines().size(),0);rejects(()->a.confirm(id));eq(a.find("A").orElseThrow().quantity(),2);});
        System.out.println("TOTAL: "+count+" passed, 0 failed");
    }
}
