# Windows demonstration — 25 November 2026

Original GUI captures were supplied by the team and are available in the [screenshots folder](screenshots/). The core recheck was run independently with Java 21/Linux.

| Evidence | Expected | Observed | Result |
|---|---|---|---|
| 01-restock.png | PM-101 18 + 4 = 22 | Balance 22, movement recorded | Pass |
| 02-new-item.png | Register new item, quantity 5 | rice, ID asd, barcode 3243, price 5.00, quantity 5 | Pass |
| 03-completed-receipt.png | 5 × 24.50, discount 5%, no tax | Subtotal 122.50, discount 6.13, total 116.37; receipt displayed | Pass |
| 04-stock-after-sale.png | PM-101 becomes 17 | Balance 17, total units 82 | Pass |
| 05-insufficient-stock.png | Reject 100 units against 17 | Error and empty basket, no movement | Pass |
| 06-cancel-confirmation.png | Warn before cancelling pending sale | Confirmation dialog | Observed |
| 07-activity-log.png | Record cancellation and no deduction | CANCELLED and COMPLETED, seven movements, PM-101 balance 17 | Pass |
| 08-maven-build-success.png and log | Compile JavaFX sources and test source | BUILD SUCCESS, 14 main sources and one test source compiled at release 21; no Surefire test count | Build pass only |

Recommended additional GUI checks: invalid zero quantity; duplicate ID/barcode; multiple-item checkout. These are covered by the separate core test runner, not claimed as Windows GUI observations. Formal performance and usability NFR experiments were not conducted (Route A selected).
