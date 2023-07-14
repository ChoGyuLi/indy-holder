package kr.co.bdgen.indywrapper.config

import android.content.Context
import org.apache.commons.io.FileUtils
import org.hyperledger.indy.sdk.pool.Pool
import org.hyperledger.indy.sdk.pool.PoolJSONParameters
import java.io.File
import java.io.FileWriter

object PoolConfig {
    //1 - for Indy Node 1.3
    //2 - for Indy Node 1.4 and greater
    private const val PROTOCOL_VERSION = 2
    private const val POOL_NAME = "holder_pool"

    @JvmStatic
    fun getPoole(context: Context): String {
        return if (FileUtils.getFile("${context.dataDir}/.indy_client/pool/$POOL_NAME").isDirectory) POOL_NAME
        else createPool(context)
    }

    private fun createPool(context: Context): String {
        Pool.setProtocolVersion(PROTOCOL_VERSION).get()
        val genesisTxnPath = createGenesisTxnFile(context).absolutePath
        Pool.createPoolLedgerConfig(
            POOL_NAME,
            PoolJSONParameters.CreatePoolLedgerConfigJSONParameter(genesisTxnPath).toString()
        ).get()
        return POOL_NAME
    }

    private fun createGenesisTxnFile(context: Context): File {
        val genesisFilePath = "${FileUtils.getTempDirectoryPath()}/indy/genesis.txn"
        val input = context.assets.open("genesis.txn").reader().readText()
        val file = File(genesisFilePath)
        FileUtils.forceMkdirParent(file)
        val fileWriter = FileWriter(file)
        fileWriter.write(input)
        fileWriter.close()
        return file
    }
}