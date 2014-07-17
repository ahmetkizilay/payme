# payme
utility module for Android to handle in-app purchases through Google Play Store.

The module contains
+ **PaymentViewGroup**:
+ **PaymentDialogFragment**: 
+ **ThankYouDialogFragment**:


#### Usage
In order to use this module in your Android Studio, you should first reference it from your project. 

Modify ```build.gradle``` file to specify the directory to the donations.aar file. In my case, I put my aar files in a directory called aars under the project directory.

```
repositories {
    flatDir { dirs 'aars' }
}
```
Then add the module dependency declaration:
```
dependencies {
    ...
    compile(name: 'donations', ext: 'aar')
    ...
}
```

Now you can start using the module.
```PaymentDialogFragment``` class takes the array resource of the product ids of your in-app purchase items. In the ```arrays.xml``` file, define your product ids.
```xml
<array name="product_ids">
    <item>payme_coffee</item>
    <item>payme_sandwich</item>
    <item>payme_soundcloud</item>
    <item>payme_github</item>
    <item>payme_genymotion</item>
    <item>payme_device</item>
    <item>payme_computer</item>
</array>
```
Create a new instance of the ```ProductDialogFragment``` class with the previously defined product ids array resource as an argument and show it like you would any fragment instance:
```java
FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
Fragment prev = getSupportFragmentManager().findFragmentByTag(DONATE_DIALOG);
if(prev != null) {
    ft.remove(prev);
}
ft.addToBackStack(null);

final PaymentDialogFragment frgDonationsDialog = PaymentDialogFragment.getInstance(R.array.product_ids);
frgDonationsDialog.show(ft, DONATE_DIALOG);
```

To listen for successful payment events, for example, to thank your donors implement the ```PaymentDialogFragment.PaymentCompletedListener``` interface:
```java
frgDonationsDialog.setPaymentCompletedListener(new PaymentDialogFragment.PaymentCompletedListener() {
    public void onPaymentCompleted() {
       frgDonationsDialog.dismiss();
       showThankYouDialog();
    }
});
```

Finally, you need to listen for the result for the buy intent. The buy intent is sent from ```PaymentDialogFragment```, but the result will first arrive at the parent activities ```onActivityResult()``` method. Therefore, you need modify the method to redirect the result into out Dialog Fragment. ```PaymentDialogFragment``` send the intent with the ```PaymentDialogFragment.PAYMENT_RESULT_CODE``` request code. You can use that constant to redirect the proper response.

```java
protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
	super.onActivityResult(requestCode, resultCode, data);

    if(requestCode == PaymentDialogFragment.PAYMENT_RESULT_CODE) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(DONATE_DIALOG);
        if (fragment != null)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}    
```
You can find a complete example in the ```app``` section of the project. Additionally, you can check out my [AndrOSC](https://github.com/artsince/AndrOSC) and [PhotoStrips](https://github.com/artsince/PhotoStrips) repositories to see further examples.
