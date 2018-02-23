package store.bitcoin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._37coins.bcJsonRpc.BitcoindClientFactory;
import com._37coins.bcJsonRpc.BitcoindInterface;

import store.bitcoin.pojo.StoredBlock;
import store.bitcoin.pojo.StoredTransaction;

public class DBBlockStoreTest {
	private static final Logger log = LoggerFactory.getLogger(DBBlockStoreTest.class);
	private static final String host = "localhost";
	private static final String rpcuser = "test";
	private static final String rpcpassword = "test";
	private static DBBlockStore store;
	private static BitcoindInterface client;
	private static StoreLoader storeLoader;

	@BeforeClass
	public static void setUpBeforeClass() throws BlockStoreException, MalformedURLException, IOException {
		store = new DBBlockStore();
		BitcoindClientFactory clientFactory = new BitcoindClientFactory(new URL("http://" + host + ":18332/"), rpcuser,
				rpcpassword);
		client = clientFactory.getClient();
		storeLoader = new StoreLoader(store, client);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
//		store.resetStore();
		int n = 6 * 24 * 60; // load data from the past two months
		log.info("blockCount: {}. Loading {} blocks.", client.getblockcount(), n);
		storeLoader.loadStore(n);
		// iterate through the blocks in the store
		StoredBlock block = store.getChainHead();
		while (block != null) {
			log.info("block: {}", block);
			block = store.get(block.getPreviousblockhash());
		}
		// get tx from known address of a user
		List<String> addresses = new LinkedList<String>();
		addresses.add("mofhdVSgsUsVacWsf8QMNhDQqYnVXPtnZH");
		List<StoredTransaction> txs = store.getTx(addresses);
		for(StoredTransaction tx:txs) {
			log.info(tx.toString());
		}
	}
}
