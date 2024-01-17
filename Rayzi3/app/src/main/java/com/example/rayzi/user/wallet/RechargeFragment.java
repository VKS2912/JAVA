package com.example.rayzi.user.wallet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.example.rayzi.R;
import com.example.rayzi.SessionManager;
import com.example.rayzi.activity.BaseFragment;
import com.example.rayzi.bottomsheets.BottomSheetPaymentMathod;
import com.example.rayzi.databinding.BottomSheetCardBinding;
import com.example.rayzi.databinding.BottomSheetPaymentBinding;
import com.example.rayzi.databinding.FragmentRechargeBinding;
import com.example.rayzi.modelclass.DiamondPlanRoot;
import com.example.rayzi.modelclass.SettingRoot;
import com.example.rayzi.modelclass.UserRoot;
import com.example.rayzi.retrofit.Const;
import com.example.rayzi.retrofit.RetrofitBuilder;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.view.CardInputWidget;
import com.stripe.param.PaymentIntentCreateParams;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechargeFragment extends BaseFragment {


    private static final String TAG = "rechargefragment";
    private static final String STR_STRIPE = "stripe";
    private static BottomSheetPaymentMathod.PaymentMethodType paymentType;
    private static final String STR_GP = "google pay";
    List<String> paymentGateways = new ArrayList<>();
    SettingRoot.Setting setting;
    BottomSheetCardBinding bottomSheetCardBinding;
    String productId;

    private String country;
    private String currency;
    private PaymentMethodCreateParams paymentMethodCreateParams;
    private Stripe stripe;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    private BillingClient billingClient;
    private String planId;


    private boolean apiCalled = false;
    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            Log.d(TAG, "onPurchasesUpdated: 1");
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                Log.d(TAG, "onPurchasesUpdated: size  " + purchases.size());
                if (!purchases.isEmpty()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                handlePurchase(purchases.get(0));
                            }
                        });
                    }
                }
                for (Purchase purchase : purchases) {
                    //  Toast.makeText(WalletActivity.this, "thy gyu", Toast.LENGTH_SHORT).show();

                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    void handlePurchase(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.


        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        if (!apiCalled) {
            Log.d(TAG, "handlePurchase: qwetuioooi2wqwertyukiol==================");
            apiCalled = true;
            callPurchaseApiGooglePay(purchase);

        } else {
            Log.d(TAG, "handlePurchase: sdsd");
        }


        ConsumeResponseListener listener = (billingResult, purchaseToken) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {

                Log.d(TAG, "handlePurchase: consume");
                // Handle the success of the consume operation.
            }
        };

        billingClient.consumeAsync(consumeParams, listener);


    }

    public RechargeFragment() {
        // Required empty public constructor
    }

    SessionManager sessionManager;
    FragmentRechargeBinding binding;
    private String paymentGateway;

    private DiamondPlanRoot.DiamondPlanItem selectedPlan;
    private UserRoot.User user;
    private int price;
    private CardInputWidget cardInputWidget;
    private BottomSheetDialog bottomSheetDialog;
    private String selectedPlanId;
    private boolean isVip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_recharge, container, false);
        sessionManager = new SessionManager(getActivity());
        if (sessionManager.getSetting().getStripePublishableKey() != null && !sessionManager.getSetting().getStripePublishableKey().isEmpty()) {
            PaymentConfiguration.init(
                    getActivity(),
                    sessionManager.getSetting().getStripePublishableKey());

        }

        stripe = new Stripe(
                getActivity(),
                Objects.requireNonNull(sessionManager.getSetting().getStripePublishableKey())
        );
        setting = sessionManager.getSetting();
        if (setting.isGooglePlaySwitch()) {
            paymentGateways.add("google pay");
        }
        if (setting.isStripeSwitch()) {
            paymentGateways.add("stripe");
        }

        getUserData();

        billingClient = BillingClient.newBuilder(getActivity())
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.d(TAG, "onBillingSetupFinished: ");
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvMyCoins.setText(String.valueOf(sessionManager.getUser().getDiamond()));
                                initMain();

                            }
                        });
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Log.d(TAG, "onBillingServiceDisconnected: ");
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onCreateView: ============================================ ==  onactvity result");
                        Intent data = result.getData();
                        // Handle the result of the activity
                    }
                });


        return binding.getRoot();
    }

    private void getUserData() {
        Log.d("checkinggg ", "getData:  my wallet 171");
        Call<UserRoot> call = RetrofitBuilder.create().getUser(sessionManager.getUser().getId());
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200 && response.body().isStatus() && response.body().getUser() != null) {
                    user = response.body().getUser();
                    sessionManager.saveUser(user);
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
//ll
            }
        });
    }


    public List<String> getPaymentGateways() {
        return paymentGateways;
    }

    private void openBottomSheet(DiamondPlanRoot.DiamondPlanItem dataItem) {
//        price = dataItem.getRupee();


        if (sessionManager.getStringValue(Const.COUNTRY).equalsIgnoreCase("India")) {
            country = "IN";
            currency = "INR";
            price = dataItem.getRupee();
//            price = data.getRupee();
        } else {
            country = "US";
            currency = "USD";
            price = dataItem.getDollar();
        }

        Log.d("1122336655", "openBottomSheet: " + price);
        if (getActivity() == null) return;
        bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet)
                        .setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        BottomSheetPaymentBinding bottomSheetPaymentBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_payment, null, false);
        bottomSheetDialog.setContentView(bottomSheetPaymentBinding.getRoot());
        bottomSheetDialog.show();
        bottomSheetPaymentBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        List<String> paymentGateways = getPaymentGateways();


        if (paymentGateways.contains(STR_GP)) {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.VISIBLE);

            bottomSheetPaymentBinding.lytgooglepay.setOnClickListener(v -> {
                paymentGateway = STR_GP;
                bottomSheetDialog.dismiss();
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytgooglepay.setVisibility(View.GONE);
        }

        if (paymentGateways.contains(STR_STRIPE)) {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.VISIBLE);
            bottomSheetPaymentBinding.lytstripe.setOnClickListener(v -> {
                paymentGateway = STR_STRIPE;
                bottomSheetDialog.dismiss();
                buyItem(dataItem);
            });
        } else {
            bottomSheetPaymentBinding.lytstripe.setVisibility(View.GONE);
        }

    }

    public void setSelectedPlanId(String selectedPlanId, boolean isVip) {
        this.selectedPlanId = selectedPlanId;
        this.isVip = isVip;
    }

    private void setUpSku(String productid) {
        Log.d(TAG, "setUpSku: " + productid);
        List<String> skuList = new ArrayList<>();

        skuList.add(productid);

        Log.d(TAG, "setUpSku: skulist size==== =========================" + skuList.size());

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);

        billingClient.querySkuDetailsAsync(params.build(),
                (billingResult, skuDetailsList) -> {
                    // Process the result.
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                Log.d(TAG, "run: " + skuDetailsList.size());
                                Log.d(TAG, "run: " + skuDetailsList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (skuDetailsList.isEmpty()) {
                                return;

                            } else {

                                lunchPayment(skuDetailsList.get(0));
                            }
                            Log.d(TAG, "setUpSku: skuDetailsList ================================================ " + skuDetailsList);

                        });

                    } else {
                        Log.d(TAG, "setUpSku: get act is null");
                    }

                });
    }

    public void makeGooglePurchase(String productid) {
        if (billingClient.isReady()) {
            setUpSku(productid);
            Log.d(TAG, "makeGooglePurchase:  setUpSku(productid); ===================================================");
        } else {
            Log.d(TAG, "paymetMethord: bp not init");
        }
    }

    private void buyItem(DiamondPlanRoot.DiamondPlanItem dataItem) {
        Log.d(TAG, "buyItem: buy item caal -0-------------------------------------------======================= ");
        planId = dataItem.getId();
        setSelectedPlanId(dataItem.getId(), false);
        if (paymentGateway.equals(STR_GP)) {

            planId = dataItem.getId();
            productId = dataItem.getProductKey();
            Log.d(TAG, "buyItem: " + productId);
            setSelectedPlanId(planId, false);
            makeGooglePurchase(productId);


        } else if (paymentGateway.equals(STR_STRIPE)) {


            bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.CustomBottomSheetDialogTheme);
            bottomSheetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            bottomSheetCardBinding = DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.bottom_sheet_card, null, false);
            bottomSheetDialog.setContentView(bottomSheetCardBinding.getRoot());
            bottomSheetCardBinding.cardInputWidget.setPostalCodeEnabled(false);
            bottomSheetCardBinding.cardInputWidget.setPostalCodeRequired(false);

            bottomSheetCardBinding.btnclose.setOnClickListener(v -> bottomSheetDialog.dismiss());
            bottomSheetCardBinding.tvamount.setText(String.valueOf(dataItem.getRupee()));
            bottomSheetCardBinding.tvcoin.setText(String.valueOf(dataItem.getDiamonds()));
            bottomSheetCardBinding.btnPay.setOnClickListener(v -> {
                PaymentMethodCreateParams params = bottomSheetCardBinding.cardInputWidget.getPaymentMethodCreateParams();
                Log.d(TAG, "buyItem: params =============   " + params);
                if (params != null) {
                    binding.pd.setVisibility(View.VISIBLE);
                    Log.d(TAG, "buyItem: shreeee 0-----------------------============================");
                    new MyTask().execute();
                    Log.d("TAG", "confirmPayment: ");
                    bottomSheetDialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.entercarddetails), Toast.LENGTH_SHORT).show();
                }
            });
            bottomSheetDialog.show();

        }
    }

    private void lunchPayment(SkuDetails s) {
        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(s)
                .build();

        int responseCode = billingClient.launchBillingFlow(getActivity(), billingFlowParams).getResponseCode();

        Log.d(TAG, "lunchPayment: responseCode ==========================================" + responseCode);
    }

    private void initMain() {

        binding.pd.setVisibility(View.VISIBLE);
        Call<DiamondPlanRoot> call = RetrofitBuilder.create().getDiamondsPlan();
        call.enqueue(new Callback<DiamondPlanRoot>() {
            @Override
            public void onResponse(Call<DiamondPlanRoot> call, Response<DiamondPlanRoot> response) {
                if (response.code() == 200) {
                    if (response.body().isStatus() && !response.body().getCoinPlan().isEmpty()) {
//                        CoinPurchaseAdapter moreCoinAdapter = new CoinPurchaseAdapter(response.body().getCoinPlan(), dataItem -> openBottomSheet(dataItem));
                        CoinPurchaseAdapter moreCoinAdapter = new CoinPurchaseAdapter(response.body().getCoinPlan(), new CoinPurchaseAdapter.OnBuyCoinClickListnear() {
                            @Override
                            public void onButClick(DiamondPlanRoot.DiamondPlanItem dataItem) {
                                openBottomSheet(dataItem);
                            }
                        });
                        binding.rvRecharge.setAdapter(moreCoinAdapter);
                        binding.pd.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<DiamondPlanRoot> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

    }


//    @RequiresApi(api = Build.VERSION_CODES.R)
//    public void pay(@Nullable String paymentMethodId, @Nullable String paymentIntentId) {
//        // ...continued in the next step
//
//        JsonObject jsonObject = new JsonObject();
//        if (paymentMethodId != null) {
//            jsonObject.addProperty("payment_method_id", paymentMethodId);
//            jsonObject.addProperty("userId", sessionManager.getUser().getId());
//            jsonObject.addProperty("planId", selectedPlan.getId());
//            jsonObject.addProperty("currency", currency.toLowerCase());
//            stripePurchased(jsonObject);
//        } else {
//            jsonObject.addProperty("payment_intent_id", paymentIntentId);
//            jsonObject.addProperty("userId", sessionManager.getUser().getId());
//            jsonObject.addProperty("planId", selectedPlan.getId());
//            jsonObject.addProperty("currency", currency.toLowerCase());
//            stripePurchasedDone(jsonObject);
//        }
//    }


    private void callPurchaseApiGooglePay(Purchase purchase) {
        if (getActivity() == null) return;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("planId", selectedPlan.getId());
        jsonObject.addProperty("productId", selectedPlan.getProductKey());
        jsonObject.addProperty("packageName", getActivity().getPackageName());
        jsonObject.addProperty("token", purchase.getPurchaseToken());
        Call<UserRoot> call = RetrofitBuilder.create().callPurchaseApiGooglePayDiamond(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {

                    if (response.body().isStatus() && response.body().getUser() != null) {
                        Toast.makeText(getActivity(), "Purchased", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                        binding.tvMyCoins.setText(String.valueOf(sessionManager.getUser().getDiamond()));
                    } else {
                        Toast.makeText(getActivity(), response.message(), Toast.LENGTH_SHORT).show();
                    }


                    apiCalled = false;
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: ");
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
        super.onActivityResult(requestCode, resultCode, data);
        binding.pd.setVisibility(View.VISIBLE);
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: ");
//        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
//
//        // Handle the result here
//    }
    private void displayAlert(@NonNull String title,
                              @Nullable String message, boolean b) {
        binding.pd.setVisibility(View.GONE);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", (dialog, which) -> {
            if (b) {
                Toast.makeText(getActivity(), "Purchase Done", Toast.LENGTH_SHORT).show();
                callPurchaseDoneApi(planId, "Stripe");
            } else {
                builder.create().dismiss();
            }
        });
        builder.create().show();
    }

    public void callPurchaseDoneApi(String planId, String paymentGateway) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", sessionManager.getUser().getId());
        jsonObject.addProperty("planId", planId);
        jsonObject.addProperty("currency", paymentGateway);

        Call<UserRoot> call = RetrofitBuilder.create().purchsePlanStripeDiamons(jsonObject);
        call.enqueue(new Callback<UserRoot>() {
            @Override
            public void onResponse(Call<UserRoot> call, Response<UserRoot> response) {
                if (response.code() == 200) {
                    if (response.body().getUser() != null && response.body().isStatus() && response.body().isStatus()) {
                        Toast.makeText(getActivity(), "Purchased", Toast.LENGTH_SHORT).show();
                        sessionManager.saveUser(response.body().getUser());
                    } else {
                        Log.d(TAG, "onResponse: 285");
                        // Toast.makeText(MyWalletActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UserRoot> call, Throwable t) {
                Log.d(TAG, "onResponse: 293");
                // Toast.makeText(MyWalletActivity.this, "Something Went Wrong..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final class PaymentResultCallback
            implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<RechargeFragment> activityRef;

        PaymentResultCallback(@NonNull RechargeFragment activity) {
            activityRef = new WeakReference<>(activity);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final RechargeFragment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.d(TAG, "onSuccess: success ma jay che ==========");
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                activity.displayAlert("Payment stripe completed",
                        gson.toJson(paymentIntent), true);
                Log.d(TAG, "onSuccess: success ma payment thya che ok ===== ");
                callPurchaseDoneApi(activity.planId, "Stripe");
                Log.d(TAG, "onSuccess: coin update no API call thay che --------------------------------------");
            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed – allow retrying using a different payment method\
                Log.d(TAG, "onSuccess: payment failed ===================================================");
                activity.displayAlert("Payment failed",
                        paymentIntent.getLastPaymentError().getMessage(), false);
            } else if (status == PaymentIntent.Status.RequiresConfirmation) {
                // After handling a required action on the client, the status of the PaymentIntent is
                // requires_confirmation. You must send the PaymentIntent ID to your backend
                // and confirm it to finalize the payment. This step enables your integration to
                // synchronously fulfill the order on your backend and return the fulfillment result
                // to your client.
//                activity.pay(null, paymentIntent.getId());
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final RechargeFragment activity = activityRef.get();
            if (activity == null) {
                return;
            }
            Log.d(TAG, "onError: errorr ======================================================= " + e.getMessage());
            // Payment request failed – allow retrying using the same payment method
            activity.displayAlert("Error", e.toString(), false);
        }
    }

    private class MyTask extends AsyncTask<String, String, String> {


        private String paymentIntentClientSecret;

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground:  getStripeSecretKey===============================================" + sessionManager.getSetting().getStripeSecretKey());
//            com.stripe.Stripe.apiKey = sessionManager.getSetting().getStripeSecreteKey();
            com.stripe.Stripe.apiKey = sessionManager.getSetting().getStripeSecretKey();

            PaymentIntentCreateParams params1 =
                    PaymentIntentCreateParams.builder()
                            .setAmount((long) price * 100)
                            .setDescription(user.getId())
                            .setReceiptEmail("dreamzdevelopers2014@gmail.com")
                            //   .putExtraParam("email",sessionManager.getUser().getData().getEmail())
                            .setShipping(
                                    PaymentIntentCreateParams.Shipping.builder()
                                            .setName(user.getName())
                                            .setPhone("+1000000000")

                                            .setAddress(
                                                    PaymentIntentCreateParams.Shipping.Address.builder()
                                                            .setLine1("abc")
                                                            .setPostalCode("91761")
                                                            .setLine2("def")
                                                            .setCity("city")
                                                            .setState("sar")
                                                            .setCountry("IN")
                                                            .build())
                                            .build())
                            .setCurrency("INR")
                            .addPaymentMethodType("card")
                            .build();

            com.stripe.model.PaymentIntent paymentIntent = null;

            try {
                paymentIntent = com.stripe.model.PaymentIntent.create(params1);
            } catch (com.stripe.exception.StripeException e) {
                e.printStackTrace();
                Log.d(TAG, "startCheckout: errr 64 " + e);
            }


            paymentIntentClientSecret = paymentIntent != null ? paymentIntent.getClientSecret() : null;
            Log.d(TAG, "doInBackground:0 " + paymentIntentClientSecret);

            Log.d(TAG, "doInBackground:1 " + paymentIntentClientSecret);
            return paymentIntentClientSecret;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            binding.pd.setVisibility(View.GONE);
            CardInputWidget cardInputWidget = bottomSheetCardBinding.cardInputWidget;
            cardInputWidget.setPostalCodeRequired(false);
            cardInputWidget.setPostalCodeEnabled(false);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params != null && paymentIntentClientSecret != null) {
                Log.d(TAG, "confirmPayment: " + params.toString());
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params, paymentIntentClientSecret);
                stripe.confirmPayment(getActivity(), confirmParams);

                Log.d(TAG, "onResponse: cps == " + confirmParams.getClientSecret());
            }
        }

    }

}