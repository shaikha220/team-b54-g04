# PearlMart Milestone 2

JavaFX prototype for Add Goods (UC04) and Process Sale (UC13), including shared stock, receipts and cancellation. Install JDK 21 and Maven, then run `mvn clean javafx:run`. Run core checks with `powershell -ExecutionPolicy Bypass -File scripts/test-core.ps1` (Maven test alone does not execute these checks).

Pricing assumption: 5% discount on subtotal >= QAR 100; tax 0%; HALF_UP rounding. Data is session-only. Facade and Strategy patterns separate UI and business rules. Team review and native Visual Paradigm models remain outstanding.


## Deliverables

- [Original Windows screenshots](evidence/screenshots/)
- [Functional test evidence](evidence/)
- [UML diagrams and editable sources](design/)
- [Word report](report/PearlMart_B54_G04_M2_Report_Updated.docx) and [PDF report](report/PearlMart_B54_G04_M2_Report_Updated.pdf)
- [Scrum plan](scrum/PLAN.md)
