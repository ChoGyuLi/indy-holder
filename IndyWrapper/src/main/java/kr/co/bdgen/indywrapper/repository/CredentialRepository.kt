package kr.co.bdgen.indywrapper.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kr.co.bdgen.indywrapper.AppExecutors
import kr.co.bdgen.indywrapper.data.Credential
import kr.co.bdgen.indywrapper.data.CredentialFilter
import org.hyperledger.indy.sdk.anoncreds.Anoncreds
import org.hyperledger.indy.sdk.wallet.Wallet
import java.util.function.Consumer


class CredentialRepository() {
    fun getCredentialList(wallet: Wallet, filter: CredentialFilter?): List<Credential> {
        return Gson().fromJson(
            getRawCredentials(
                wallet,
                if (filter == null) "{}" else Gson().toJson(filter).toString()
            ),
            object : TypeToken<List<Credential>>() {}.type
        )
    }

    fun getRawCredentials(wallet: Wallet, filter: String): String {
        return Anoncreds.proverGetCredentials(wallet, filter).get()
    }

    fun deleteCredential(
        wallet: Wallet,
        credId: String,
        onSuccess: Runnable,
        onFail: Consumer<Throwable?>
    ) {
        Anoncreds.proverDeleteCredential(wallet, credId)
            .thenApply { Result.success(it) }
            .exceptionally {
                Result.failure(it)
            }
            .thenAcceptAsync(
                {
                    if (it.isSuccess) {
                        onSuccess.run()
                        return@thenAcceptAsync
                    }
                    onFail.accept(it.exceptionOrNull())
                },
                AppExecutors.getInstance().mainThread()
            )


    }
}