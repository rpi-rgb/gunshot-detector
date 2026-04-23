package com.gunshot.detector.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(context: Context, private val onProPurchased: () -> Unit) : PurchasesUpdatedListener {
    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro
    private val billingClient = BillingClient.newBuilder(context).setListener(this).enablePendingPurchases().build()
    init { billingClient.startConnection(object : BillingClientStateListener { override fun onBillingSetupFinished(result: BillingResult) { if (result.responseCode == BillingClient.BillingResponseCode.OK) queryExistingPurchases() }; override fun onBillingServiceDisconnected() { } }) }
    private fun queryExistingPurchases() { billingClient.queryPurchasesAsync(QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()) { _, purchases -> purchases.forEach { if (it.products.contains("gunshot_detector_pro") && it.purchaseState == Purchase.PurchaseState.PURCHASED) { _isPro.value = true; onProPurchased() } } } }
    fun launchPurchaseFlow(activity: Activity) { }
    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) { }
}