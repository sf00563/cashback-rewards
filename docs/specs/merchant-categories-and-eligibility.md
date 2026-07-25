# Merchant Categories & Eligibility

**As the** card rewards product manager, **I want** cashback rates to be driven by the transaction's merchant category code and to apply only to eligible transactions at partner merchants, **so that** rewards match our commercial partnerships and only reward genuine, settled spend.

## Context / Decisions
- **The MCC on the transaction is the sole rate source.** Per-merchant configured rates (from basic-cashback) are *not part of this product*. The rate is derived from the **merchant category code (MCC)** carried on each transaction — the merchant itself no longer holds a rate.
- **Merchants carry a partner status flag** (partner / non-partner) and nothing else rate-related. Only **partner** merchants earn cashback; at a **non-partner** merchant the rate resolves to **0%**, so the purchase is accepted and recorded but earns **0.00** (a valid excluded partner, not an error).
- **Rates by MCC:** Groceries (**5411**) **2%**, Fuel (**5541**) **1%**, everything else **0.5%** (default). MCC is an **open set** — any code that isn't a recognised grocery or fuel code (including unset) resolves to the 0.5% default.
- **A transaction earns cashback only when it is a posted purchase on an active card.** The transaction carries, all supplied on the request: **mcc** (numeric code), **type** (purchase / refund / fee), **status** (pending / posted), and **card state** (active / frozen / cancelled).
- **Ineligible transactions are rejected with 409** (Conflict) — the type/status/card reasons share the one status; the error message distinguishes them. Ineligible transactions are *not* recorded. (Non-partner is *not* an eligibility rejection — it is recorded at a 0% rate.)
- **A malformed transaction attribute is a 400, not a 409.** 409 means "we understood the transaction and it is not eligible"; **400** means "we could not read the transaction at all". So an unrecognised or missing **type**, **status** or **card state** → **400**. Values are matched case-insensitively (`"posted"` and `"POSTED"` are the same status).
- **mcc is the one attribute with a defined default.** Unlike the three eligibility attributes, an absent or unrecognised **mcc** is *not* an error — it resolves to the 0.5% default rate (the MCC set is open; see the rate rule).
- Inherited from basic-cashback and unchanged: unknown merchant/customer → **404**; non-positive amount → **400**; money is `BigDecimal`, `RoundingMode.DOWN`, scale 2.

---

### Rule: Should apply the cashback rate for the transaction's MCC at a partner merchant.

| MCC | Category | Cashback rate |
|---|---|---|
| 5411 | Groceries | 2% |
| 5541 | Fuel | 1% |
| (any other, or unset) | — | 0.5% (default) |

At a **non-partner** merchant the rate is gated to **0%** regardless of MCC.

- Example: The one where a 100.00 posted purchase with MCC 5411 (groceries) on an active card at a partner merchant earns 2.00.
- Example: The one where a 100.00 posted purchase with MCC 5541 (fuel) at a partner merchant earns 1.00.
- Counter-example: The one where the MCC is unrecognised → the 0.5% default applies (a valid outcome, not an error) — 100.00 earns 0.50.
- Counter-example: The one where the merchant is a non-partner → the rate is gated to 0%, so a 100.00 grocery-MCC purchase earns 0.00 (recorded, not rejected).

### Rule: Must only award cashback on an eligible transaction — a posted purchase on an active card.

Eligibility requires **all three**: type = purchase, status = posted, card = active. Any single deviation makes the transaction ineligible. (Partner status is *not* an eligibility condition — see the rate rule.)

| Type | Status | Card state | Outcome |
|---|---|---|---|
| Purchase | Posted | Active | Eligible — cashback awarded by MCC |
| Purchase | Pending | Active | Rejected (409) |
| Purchase | Posted | Frozen | Rejected (409) |
| Purchase | Posted | Cancelled | Rejected (409) |
| Refund | Posted | Active | Rejected (409) |
| Fee | Posted | Active | Rejected (409) |
| *unrecognised or missing* | any | any | Rejected (**400**) |
| any | *unrecognised or missing* | any | Rejected (**400**) |
| any | any | *unrecognised or missing* | Rejected (**400**) |

An attribute value that isn't one of the listed options can't be judged eligible or ineligible, so it is a malformed request (**400**) rather than an eligibility conflict (409).

- Example: The one where a posted purchase on an active card is accepted and credits cashback.
- Counter-example: The one where every attribute qualifies *except* the card is frozen → rejected (409), even though MCC and status would otherwise earn cashback (a valid business exclusion, not a bug).
- Counter-example: The one where the type is not a recognised transaction type (`chargeback`) → rejected (400), distinct from an eligibility conflict (409).
- Counter-example: The one where the status is not a recognised transaction status (`settled`) → rejected (400).
- Counter-example: The one where the card state is not a recognised card state (`expired`) → rejected (400).
- Counter-example: The one where the card state is missing altogether → rejected (400) — unlike an absent mcc, which defaults.

### Rule: Must reject an eligible-looking transaction that references an unknown merchant or customer, or an invalid amount.

- Example: The one where an otherwise-eligible posted purchase names a merchant ID that doesn't exist → rejected (404).
- Example: The one where an otherwise-eligible posted purchase has a 0.00 amount → rejected (400), distinct from an eligibility conflict (409).
- Counter-example: The one where the customer ID doesn't exist → rejected (404), distinct from an invalid amount (400).
