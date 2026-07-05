# Basic Cashback Calculation

**As a** customer, **I want** to earn cashback on my purchases **so that** I'm rewarded for shopping with partner merchants.

## Context / Decisions
- Merchant cashback rate is a **percentage per merchant** (e.g. `5` = 5%), valid range **0%–100%** inclusive (0% = valid excluded partner).
- Purchase references an **existing merchant by ID** (unknown → 404) and an **existing customer by ID** (unknown → 404).
- Purchase amount must be **strictly positive** (`> 0.00`); zero/negative → 400.
- Customer has a single **running BigDecimal balance**; cashback is added to it.
- **Partial refunds** allowed; cashback reversed proportionally. Reversal **clamps at a zero balance** (never negative). Refunds reference a purchase by ID; cumulative refunds may not exceed the original amount.
- Money: `BigDecimal`, `RoundingMode.DOWN`, scale 2 (per CLAUDE.md).

---

### Rule: Should calculate cashback as the merchant's percentage applied to the purchase amount, rounded down to 2 decimals.

| Purchase amount | Merchant rate | Cashback |
|---|---|---|
| 100.00 | 5% | 5.00 |
| 33.33 | 5% | 1.66 |
| 0.01 | 5% | 0.00 |

- Counter-example: The one where a tiny purchase rounds down to 0.00 — a valid outcome, not an error.

### Rule: Should apply the cashback rate configured for the purchase's merchant.

- Example: The one where the same 100.00 purchase yields 5.00 at a 5% merchant but 2.00 at a 2% merchant.
- Counter-example: The one where the merchant is configured at 0% → cashback is 0.00 (valid excluded partner).

### Rule: Should credit the calculated cashback to the customer's running rewards balance.

- Example: The one where a customer with a 10.00 balance earns 5.00 and ends at 15.00.
- Counter-example: The one where cashback rounds to 0.00 → balance is unchanged.

### Rule: Must reject a purchase with a non-positive amount.

- Example: The one where the purchase amount is 0.00 → rejected (400).
- Example: The one where the purchase amount is negative → rejected (400).

### Rule: Must reject a purchase referencing an unknown merchant or customer.

- Example: The one where the merchant ID doesn't exist → rejected (404).
- Counter-example: The one where the customer ID doesn't exist → rejected (404), distinct from an invalid amount (400).

### Rule: Should reverse cashback from the customer's balance when a purchase is refunded.

| Original purchase | Rate | Cashback earned | Refunded | Cashback reversed |
|---|---|---|---|---|
| 100.00 | 5% | 5.00 | 100.00 (full) | 5.00 |
| 100.00 | 5% | 5.00 | 40.00 (partial) | 2.00 |

- Example: The one where a customer earned 5.00 (balance 5.00), the purchase is fully refunded, and the balance returns to 0.00.
- Counter-example: The one where the customer already redeemed the cashback (balance 1.00 < 5.00 to reverse) → balance clamps at 0.00, not negative.

### Rule: Must reject an invalid refund.

- Example: The one where the refund references an unknown purchase → rejected (404).
- Example: The one where cumulative refunds would exceed the original purchase amount → rejected (409).
