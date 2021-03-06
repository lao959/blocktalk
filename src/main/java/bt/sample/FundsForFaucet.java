package bt.sample;
import bt.Contract;
import bt.ui.EmulatorWindow;
import bt.Address;
import bt.BT;
import bt.Emulator;
import bt.compiler.CompilerVersion;
import bt.compiler.TargetCompilerVersion;


/**
 * Raising funds for burst faucet.
 * 
 * This contract allows users to supply bursts for FFF token and withdral their bursts by transfering back the FFFs. 
 * The contact will send bursts for faucet distrucation when it more than PAYTHRESHOLD.
 * 
 * @author lao
 *
 */
@TargetCompilerVersion(CompilerVersion.v0_0_1)
public class FundsForFaucet  extends Contract {
    static final long   ONE_PRECENT = 10000L; 
    static final String ANAME = "FFF";
    static final String ADESC = "Raising funds for burst faucet";
    static final int    ADECIMALS = 8;    
    static final long   AQUANTITY = 1_000_000_000L * ONE_BURST;
    static final int    EARNRATE = 10;
    static final long   PAYTHRESHOLD = 1000L * ONE_BURST;
    static final int    PERCENTAGETOPAY = 50;
    static final long   INITEXCHANGERATE = 50 * ONE_PRECENT; 
    static final String MSGTOSUPPLIER = "Thank you for supporting the Faucet";

    long coinId = 0L;
    Address holdersContract;
    long earnPayable = 0L;
    long earnPaid = 0L;
    long currentExchangeRate = INITEXCHANGERATE;

    Address txSender;
    long txAmount;
    long txAssetId;
 
    public FundsForFaucet(){

        holdersContract = getCreator();
    }

    private void supply(){
 
        if(txAmount > 0){

            long amountForCoin = txAmount * (100 - EARNRATE)/100;
            earnPayable += txAmount - amountForCoin;

            mint(0, MSGTOSUPPLIER, coinId, amountForCoin * currentExchangeRate / ONE_PRECENT, txSender);

            SendFundsToFaucet();
        }
    }

    private void withdrawal(){

        long amount = getCurrentTxAssetAmount() * ONE_PRECENT / currentExchangeRate;
        if(amount > 0)
            sendAmount(amount, txSender);
    }

    private void SendFundsToFaucet(){

        if( earnPayable > PAYTHRESHOLD ){
            sendAmount( earnPayable * PERCENTAGETOPAY / 100, holdersContract);
        }

    }

    private void IssueFFF(){
        coinId = mold(ANAME, ADESC, AQUANTITY, ADECIMALS);
        if(coinId > -100 && coinId < 0){
            //failed to issue fff
            coinId = 0L;
        }
    }

    private void refund(){

        if(txAmount > 0)
            sendAmount(txAmount, txSender);
    }
    
    public void txReceived(){

        txAmount = getCurrentTxAmount();
        txSender = getCurrentTxSender();

        if(coinId == 0L )
        {
            if(txSender == holdersContract){

                //issue the coin if the holders send funds        
                IssueFFF();
            }
            else
            {
                //return funds if the coin is not ready.
                refund();
            }
        }
        else{

            //to supply by only sending burstcoin
            txAssetId = getCurrentTxAssetId();
            if( txAssetId != coinId){

                supply();
            }
            else{

                //to withdrawal by sending back the FFF
                withdrawal();
            }
        }
    }

    public static void main(String[] args) throws Exception {
    	
        //compile();


        BT.activateCIP20(true);

        // some initialization code to make things easier to debug
        Emulator emu = Emulator.getInstance();
        Address creator = Emulator.getInstance().getAddress("CREATOR");
        emu.airDrop(creator, 10000*Contract.ONE_BURST);
        Address supplier = Emulator.getInstance().getAddress("SUPPLIER");
        emu.airDrop(supplier, 10000*Contract.ONE_BURST);

        Address fun = emu.getAddress("FUN");
        emu.createConctract(creator, fun, FundsForFaucet.class, Contract.ONE_BURST);
        emu.forgeBlock();

        emu.send(creator, fun, 1001 * Contract.ONE_BURST);
        emu.forgeBlock();

        new EmulatorWindow(FundsForFaucet.class);
    }
}
