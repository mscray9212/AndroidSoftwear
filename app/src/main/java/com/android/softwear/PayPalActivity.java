package com.android.softwear;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.json.JSONException;
import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.softwear.process.ConnectDB;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;

import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


public class PayPalActivity extends AppCompatActivity {
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private String user;
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "AYvYosxk6ClBvxNBoQS770jaGeEsfoQfjFAELlk1ZxDD3vbz0HhhSKkGx8WFeen2khaT60vZJHM7H0Op";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
            .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));

    float totalAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        user = MainActivity.currentAccount.getUsername();

        Intent cartActivity = getIntent();
        totalAmount = cartActivity.getFloatExtra("totalAmount", 100);

        Log.d("PAYPALACTIVITY", Float.toString(totalAmount));
        Log.d("USERNAME", user);
        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        startService(intent);
    }

    public void onBuyPressed(View pressed) {

        // PAYMENT_INTENT_SALE will cause the payment to complete immediately.
        // Change PAYMENT_INTENT_SALE to
        //   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
        //   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
        //     later via calls from your server.

        PayPalPayment payment = new PayPalPayment(BigDecimal.valueOf(totalAmount), "USD", "Thank You for shopping with us!",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    /*
    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(PayPalActivity.this, PayPalFuturePaymentActivity.class);

        startctivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i("Payment Process", confirm.toJSONObject().toString(4));


                        Toast.makeText(PayPalActivity.this, "Payment Successful. You will receive an email shortly", Toast.LENGTH_SHORT).show();

                        Log.i("Payment Process", "clear cart");

                        // clear Shopping Cart and go back to Search screen
                        new clearShoppingCart().execute();


                    } catch (JSONException e) {
                        //Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Payment Process", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("Payment Process", "An invalid Payment was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("Payment Process", auth.toJSONObject().toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("Payment Process", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(), "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("Payment Process", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Payment Process", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("Payment Process",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }



    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

        // TODO:
        // Send the authorization response to your server, where it can exchange the authorization code
        // for OAuth access and refresh tokens.
        //
        // Your server must then store these tokens, so that your server code can execute payments
        // for this user in the future.

    }


    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    // Added clearShopping Cart: when checkout_btn pressed - shopping cart is clear

    public class clearShoppingCart extends AsyncTask<Void, Void, Void> {

        String queryResult = "";
        ProgressDialog pDialog;

        protected void onPreExecute() {
            //bench warmer
            if(user == null || user.equals("Guest")) {
                pDialog = new ProgressDialog(PayPalActivity.this);
                pDialog.setTitle("Login First");
                pDialog.setMessage("Please, sign in to use cart services");
                pDialog.setCancelable(true);
                pDialog.show();
            }
        }

        protected Void doInBackground(Void... arg0)  {

            if(user != null && !(user.equals("Guest"))) {
                try {
                    Connection conn = ConnectDB.getConnection();
                    String queryString = "DELETE FROM Orders where User_Name = ?";
                    PreparedStatement st = conn.prepareStatement(queryString);
                    st.setString(1, user);
                    st.executeUpdate();

                    queryResult = "All purchased items are deleted from Orders";
                    conn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    queryResult = "Database connection failure!\n" + e.toString();
                }

                Log.i("Payment Process", queryResult);
            }
            else {
                pDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            startActivity(new Intent(getApplicationContext(), Search.class));
        }

    }

}
