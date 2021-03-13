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
 * 
 * @author lao
 *
 */
@TargetCompilerVersion(CompilerVersion.v0_0_1)
public class FundsForFaucet  extends Contract {

    public static final String ANAME = "FFF";
    public static final String ADESC = "Hold FFF to support BURST";
    public static final int    ADECIMALS = 8;    
    public static final long   ACAPABILITY = 1_000_000_000L * ONE_BURST;

    public static final long   PAYTHRESHOLD = 1000L * ONE_BURST;
    public static final int    PERCENTAGETOPAY = 20;
    public static final String MSGTOSUPPLIER = "Thank you for supporting.";
    public static final String MSGFAILTOWITHDRAL = "Sorry, no enough BURST.";

    long coinId = 0L;
    Address holdersContract;

    Address txSender;
    long txAmount;
 
    public FundsForFaucet(){

        holdersContract = getCreator();
    }

    private void supply(){
 
        if(txAmount > 0){

            if(txAmount < getAssetMintableBalance(coinId)){

                mint(0, MSGTOSUPPLIER, coinId, txAmount, txSender);

                SendFundsToFaucet();
            }
            else
                refund();
        }
    }

    private void withdrawal(){

        long amount = getCurrentTxAssetAmount();
        if(amount > 0 && amount < getCurrentBalance()) 
            sendAmount(amount, txSender);
        else{
            mint(0, MSGFAILTOWITHDRAL, coinId, amount, txSender);
        }
    }

    private void SendFundsToFaucet(){

        if( getCurrentBalance() > PAYTHRESHOLD ){
            sendAmount( getCurrentBalance() * PERCENTAGETOPAY / 100, holdersContract);
        }

    }

    private void MoldFFF(){
        coinId = mold(ANAME, ADESC, ACAPABILITY, ADECIMALS);
        if(coinId > -100 && coinId < 0){
            //failed to mold fff
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
                MoldFFF();
            }
            else
            {
                //return funds if FFF is not ready.
                refund();
            }
        }
        else{

            //to supply by only sending Burst
            if( getCurrentTxAssetId() != coinId){

                supply();
            }
            else{

                //to withdrawal by sending back the FFF
                withdrawal();
            }
        }
    }

    public static void main(String[] args) throws Exception {
    	
        compile();


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
