package bt;

import burst.kit.entity.response.AT;
import burst.kit.entity.response.Asset;
import org.junit.Test;

import bt.compiler.Compiler;
import bt.sample.FundsForFaucet;
import burst.kit.entity.BurstAddress;
import burst.kit.entity.BurstID;
import burst.kit.entity.BurstValue;

import static org.junit.Assert.*;

/**
 * We assume a localhost testnet with 0 seconds mock mining is available and current block height is geater than 760000  for the
 * tests to work.
 * 
 * @author lao
 */
public class FundsForFaucetTest extends BT {

    public static void main(String[] args) throws Exception {
        FundsForFaucetTest t = new FundsForFaucetTest();

        t.testFFF();
    }

    @Test
    public void testFFF() throws Exception {

        BurstValue atActiveFee = BurstValue.fromPlanck(Contract.ONE_BURST);
        BurstValue twoBurst = BurstValue.fromPlanck(2*Contract.ONE_BURST);

        //get some balance
        BT.forgeBlock();
        BT.forgeBlock();
        BT.forgeBlock();


        //register FundsForFaucet
        Compiler comp = BT.compileContract(FundsForFaucet.class);

        String name = FundsForFaucet.class.getSimpleName() + System.currentTimeMillis();
        BurstAddress creator = BT.getBurstAddressFromPassphrase(BT.PASSPHRASE);

        BT.registerContract(BT.PASSPHRASE, comp, name, name, atActiveFee,
                BurstValue.fromBurst(0.735), 1000).blockingGet();
        BT.forgeBlock();

        AT contract = BT.findContract(creator, name);
        System.out.println( "AT Id: " + contract.getId().getID());

        //creator send two BURST to mold FFF
        BT.sendAmount(BT.PASSPHRASE, contract.getId(), twoBurst).blockingGet();;
        BT.forgeBlock();
        BT.forgeBlock();

        long owner = BT.getContractFieldValue(contract, comp.getField("holdersContract").getAddress());
        long coinId = BT.getContractFieldValue(contract, comp.getField("coinId").getAddress());
        assertEquals(creator.getSignedLongId(), owner);
        assertNotEquals(0L, coinId);

        Asset fff = BT.getAsset(BurstID.fromLong(coinId));
        assertNotEquals(null, fff);
        assertEquals(FundsForFaucet.ANAME, fff.getName());
        //TO-DO: burst.kit need to upgrade to get asset quantity owned by at AT
        //assertEquals(FundsForFaucet.ACAPABILITY, fff.getQuantity());
        assertEquals(FundsForFaucet.ADECIMALS, fff.getDecimals());
        assertEquals(contract.getId().getBurstID(), fff.getAccountId());

        // a devotee send 100 BURST to AT to mint FFF
        BurstAddress devotee = BT.getBurstAddressFromPassphrase(BT.PASSPHRASE2);
        BT.forgeBlock(BT.PASSPHRASE2);
        BT.forgeBlock(BT.PASSPHRASE2);
        BT.sendAmount(BT.PASSPHRASE2, contract.getId(), BurstValue.fromPlanck(101*Contract.ONE_BURST)).blockingGet();;
        BT.forgeBlock();
        BT.forgeBlock();

        BurstValue banlance = BT.getAccountAssetBanlance(devotee, BurstID.fromLong(coinId));
        System.out.println( "A devotee mint FFF: " + banlance.toFormattedString());
        assertTrue("Mint FFF failed.", banlance.longValue() > 0);

        //adevotee send 10 FFF back to AT to burn down
        //TO-DO: burst.kit need to upgrade to support asset transfer with BURST 
        sendAmountAndAsset(BT.PASSPHRASE2, contract.getId(), twoBurst, BurstValue.fromBurst(0.735),BurstID.fromLong(coinId), BurstValue.fromPlanck(10*Contract.ONE_BURST)).blockingGet();
        BT.forgeBlock();
        BT.forgeBlock();

        //Go to wallet to verify adevotee's FFF balance manually 


    }
}
