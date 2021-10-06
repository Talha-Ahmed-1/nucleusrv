package components
import chisel3._
import caravan.bus.common.{BusConfig, AbstrRequest, AbstrResponse, BusHost, BusDevice}
import caravan.bus.wishbone.{WishboneConfig, WBRequest, WBResponse}
import jigsaw.rams.fpga.BlockRam

class Top(/*val req:AbstrRequest, val rsp:AbstrResponse,val instrAdapter:Module, val dataAdapter:Module ,*/ programFile:Option[String]) extends Module{
  val io = IO(new Bundle() {
    val pin = Output(UInt(32.W))
  })
  implicit val config = WishboneConfig(32,32)

//  val imem: InstructionMemory = Module(new InstructionMemory)
//  val dmem: DataMemory = Module(new DataMemory)
  val core: Core = Module(new Core(/*req, rsp*/ new WBRequest,new WBResponse()))
  val imemAdapter = Module(new WishboneAdapter()) //instrAdapter
  val dmemAdapter = Module(new WishboneAdapter()) //dmemAdapter

  // TODO: Make RAMs generic
  val imemCtrl = Module(BlockRam.createNonMaskableRAM(programFile, config, 1024))
  // val dmemCtrl = Module(BlockRam.createMaskableRAM(config, 1024))
  val sramCtrl = Module(new MemoryWrapper(new WBRequest, new WBResponse))

  /*  Imem Interceonnections  */
  imemAdapter.io.reqIn <> core.io.imemReq
  core.io.imemRsp <> imemAdapter.io.rspOut
  imemCtrl.io.req <> imemAdapter.io.reqOut
  imemAdapter.io.rspIn <> imemCtrl.io.rsp

  /*  Dmem Interconnections  */
  dmemAdapter.io.reqIn <> core.io.dmemReq
  core.io.dmemRsp <> dmemAdapter.io.rspOut
  // dmemCtrl.io.req <> dmemAdapter.io.reqOut
  // dmemAdapter.io.rspIn <> dmemCtrl.io.rsp

  sramCtrl.io.request <> dmemAdapter.io.reqOut
  dmemAdapter.io.rspIn <> sramCtrl.io.response

//  core.io.imem <> imem.io
//  core.io.dmem <> dmem.io
  io.pin := core.io.pin

}
