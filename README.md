# SDK for Android
The ECRHubClient Android SDK makes it quick and easy to connect CodePay Register APP to your ECR Android APP. We offer many ways to connect to the CodePay Register APP to quickly realize the cashiering capabilities of your ECR system.With the ECRHub Android SDK you can use CodePay Register's payments, refunds, queries and more!
## Getting Started
Get started with our [SDK reference](https://github.com/paycloud-open/ecr-hub-client-sdk-android/tree/main/wisecashier_ecr_sdk) or [example projects](https://github.com/paycloud-open/ecr-hub-client-sdk-android) or [integration guides](./posDevGuidelines)


## Features

* **Multiple Connection Mode**，This SDK provides multiple ways to connect to Wise Cashier, including Cloud, Serial and LAN. You can choose the applicable connection method according to the ECR system environment situation。

* **Payment**， This SDK provides a variety of payment methods, including code scanning bank cards, and also provides a lot of medium payment capabilities, such as consumption, revocation, refund, pre-authorization, pre-authorization completion and so on.
* **Client Manager**，In LAN mode, WiseCashier acts as a Server, and the SDK is capable of connecting to multiple Servers, and the SDK provides methods to manage the Servers.



## Installation
**Requirements**

* Android 8.0 (API level 24) and above

**Configuration**
* Add it in your root `build.gradle`  at the end of repositories:
```java
allprojects {
                repositories {
                        ...
                        maven { url 'https://jitpack.io' }
                }
        }
}
```
* Add `ecrhub-android` to your app `build.gradle` dependencies.

```java

dependencies {
                implementation 'com.github.paycloud-open:ecr-hub-client-sdk-android:1.0.1'
        }
```

## API List

### Create Client

`ECRHubClient` is the Client connection object, Connection and Communicate with a single service node.

```java
import com.wisecashier.ecr.sdk.client.ECRHubConfig
import com.ecr.hub.client.ECRHubClient
// Create a client instance
// Automatically connect
// By WebSocket method
val config = ECRHubConfig()
/**
* @param ip the Service ip
* @param config server configuration
* @param  listener the connect status listener
*/
mClient = ECRHubClient("ws://ip:port", config, this)
```
### Connect
  A global listener is registered when the client object is created, which is used to listen to the status of the connection
  ```java
mClient.connect()
```
### DisConnect
A global listener is registered when the client object is created, which is used to listen to the status of the connection
```java
mClient.disConnect()
```
### Purchase
 `PaymentParams` is a class that manages payment parameters;`Payment` is a payment management class that is created when the Client is created.`
```java
 val params = PaymentParams()
params.transType = 1
params.appId = "you payment app id"
merchantOrderNo = "123" + getCurDateStr("yyyyMMddHHmmss")
params.merchantOrderNo = merchantOrderNo
params.payMethod = "BANKCARD"
params.transAmount = "2"
params.msgId = "111111"
val voiceData = params.voice_data
voiceData.content = "Received a new order"
voiceData.content_locale = "en-US"
params.voice_data = voiceData
mClient.payment.purchase(params, object :
    ECRHubResponseCallBack {
    override fun onError(errorCode: String?, errorMsg: String?) {
      //TODO:Payment Error
    }

    override fun onSuccess(data: String?) {
      //TODO:Payment Success
    }
})
```
### Refund
Refund transactions can be done for successfully paid orders
```java
val params = PaymentParams()
params.transType = 2
params.appId = "you payment app id"
params.origMerchantOrderNo = "the purchase mechant order no"
params.transAmount = "2"
val voiceData = params.voice_data
voiceData.content = "Received a new order"
voiceData.content_locale = "en-US"
params.voice_data = voiceData
mClient.payment.purchase(params, object :
    ECRHubResponseCallBack {
    override fun onError(errorCode: String?, errorMsg: String?) {
     //TODO:Refund Error
    }

    override fun onSuccess(data: String?) {
       //TODO:Refund Success
    }
})
```

### Query Order
For transactions that do not return a payment result and you want to get the result of the transaction, you can call this query method
```java
val params = PaymentParams()
params.origMerchantOrderNo = "the transactions mechant order no"
params.appId = "you payment app id"
mClient.payment.query(params, object :
    ECRHubResponseCallBack {
    override fun onError(errorCode: String?, errorMsg: String?) {
      //TODO:Query Error
    }

    override fun onSuccess(data: String?) {
    //TODO:Query Success
    }
})
```
### Close Order
After initiating a transaction, this method can be called to cancel the payment until the transaction is successful
```java
val params = PaymentParams()
params.origMerchantOrderNo = "the transactions mechant order no"
params.appId = "you payment app id"
mClient.payment.close(params, object :
ECRHubResponseCallBack {
override fun onError(errorCode: String?, errorMsg: String?) {
//TODO:Close Error
}

override fun onSuccess(data: String?) {
//TODO:Close Success
}
})
```


## Version history

* `1.0.1` 
    Publish date：2023-09-21