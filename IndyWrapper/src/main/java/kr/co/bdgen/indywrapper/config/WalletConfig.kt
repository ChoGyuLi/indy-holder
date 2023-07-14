package kr.co.bdgen.indywrapper.config

import android.content.Context
import org.apache.commons.io.FileUtils
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.did.Did
import org.hyperledger.indy.sdk.wallet.Wallet
import org.json.JSONObject
import java.util.concurrent.CompletableFuture


object WalletConfig {
    private const val WALLET_ID = "holder_wallet_id"
    private const val WALLET_KEY = "holder_wallet_key"

    private val walletConfig = JSONObject().put("id", WALLET_ID).toString()
    private val walletCredentials = JSONObject().put("key", WALLET_KEY).toString()

    @JvmStatic
    fun createWallet(context: Context): CompletableFuture<Void> {
        if (FileUtils.getFile("${context.dataDir}/.indy_client/wallet/$WALLET_ID").isDirectory) {
            return CompletableFuture.completedFuture(null)
        }
        Wallet.generateWalletKey(walletConfig).get()
        return Wallet.createWallet(walletConfig, walletCredentials)
    }

    @JvmStatic
    fun openWallet(): CompletableFuture<Wallet> {
        return Wallet.openWallet(walletConfig, walletCredentials)
    }

    @JvmStatic
    fun createMasterSecret(wallet: Wallet, masterSecretId: String? = null): String =
        Anoncreds.proverCreateMasterSecret(wallet, masterSecretId).get()

    @JvmStatic
    fun createDid(wallet: Wallet): CompletableFuture<Pair<String, String>> {
        return Did.createAndStoreMyDid(wallet, "{}")
            .thenApply {
                return@thenApply Pair(it.did, it.verkey)
            }
    }
}