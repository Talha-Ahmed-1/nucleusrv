package nucleusrv.components

import chisel3._
import chisel3.util._

object compressed{
    def CLW       = BitPat("b010???????????00")
    def CLWSP     = BitPat("b010???????????10")
    def CSW       = BitPat("b110???????????00")
    def CSWSP     = BitPat("b110???????????10")
    def CJ        = BitPat("b101???????????01")
    def CJAL      = BitPat("b001???????????01")
    def CJR       = BitPat("b1000?????0000010")
    def CJALR     = BitPat("b1001?????0000010")
    def CBEQZ     = BitPat("b110???????????01")
    def CBNEQZ    = BitPat("b111???????????01")
    def CLI       = BitPat("b010???????????01")
    def CLUI      = BitPat("b011???????????01")
    def CADDI     = BitPat("b000???????????01")
    def CADDI16SP = BitPat("b011?00010?????01")
    def CADDI4SPN = BitPat("b000???????????00")
    def CSLLI     = BitPat("b000???????????10")
    def CSRLI     = BitPat("b100?00????????01")
    def CSRAI     = BitPat("b100?01????????01")
    def CANDI     = BitPat("b100?10????????01")
    def CMV       = BitPat("b1000??????????10")
    def CADD      = BitPat("b1001??????????10")
    def CAND      = BitPat("b100011???11???01")
    def COR       = BitPat("b100011???10???01")
    def CXOR      = BitPat("b100011???01???01")
    def CSUB      = BitPat("b100011???00???01")
    def CNOP      = BitPat("b0000000000000001")
    def CEBREAK   = BitPat("b1001000000000010")
    def CILLEGAL  = BitPat("b0000000000000000")
}

import compressed._

class CDecoderIO extends Bundle{
    val instIn = Input(UInt(32.W))
    val instOut = Output(UInt(32.W))
    val compressed = Output(Bool())
}

class CompressedDecoder extends Module{
    val io = IO(new CDecoderIO)

    val RS2 = Wire(UInt(5.W))
    RS2 := io.instIn(4,2) + 8.U
    val RD_RS1 = Wire(UInt(5.W))
    RD_RS1 := io.instIn(9,7) + 8.U

    val iImm = Wire(UInt(6.W))
    iImm := Cat(io.instIn(12), io.instIn(6,2))

    val lwImm = Wire(UInt(5.W))
    lwImm := Cat(io.instIn(5), io.instIn(12,10), io.instIn(6))

    val sbImm = Wire(UInt(8.W))
    sbImm := Cat(io.instIn(12,10), io.instIn(2), io.instIn(6,3))

    val ujImm = Wire(UInt(11.W))
    ujImm := Cat(io.instIn(10,7), io.instIn(2), io.instIn(6,3))

    val sImm = Wire(UInt(5.W))
    sImm := Cat(io.instIn(5), io.instIn(12,10), io.instIn(6))

    def SW      = Cat(sImm(4,3), RS2, RD_RS1, ("b010".U)(3.W), sImm(2,0), ("b00".U)(2.W), ("b0100011".U)(7.W))
    def JAL     = Cat(ujImm(9,0), ujImm(10), ("b00000000".U)(8.W), ("b00001".U)(5.W), ("b1101111".U)(7.W))
    def BEQZ    = Cat(sbImm(7,4), ("b000".U)(3.W), ("b00000".U)(5.W), RD_RS1, sbImm(3,0), "b0".U, ("b1100011".U)(7.W))
    def BNEZ    = Cat(sbImm(7,4), ("b001".U)(3.W), ("b00000".U)(5.W), RD_RS1, sbImm(3,0), "b0".U, ("b1100011".U)(7.W))
    def LUI     = Cat(iImm, RD_RS1, ("b0110111".U)(7.W))
    def SLLI    = Cat(iImm, RD_RS1, ("b001".U)(3.W), RD_RS1, ("b0010011".U)(7.W))
    def SRLI    = Cat(iImm, RD_RS1, ("b101".U)(3.W), RD_RS1, ("b0010011".U)(7.W))
    def SRAI    = Cat(("b0100000".U)(7.W), RD_RS1, ("b101".U)(3.W), RD_RS1, ("b0010011".U)(7.W))
    def JALR    = Cat(0.U, ("b00000".U)(5.W), ("b000".U)(3.W), RD_RS1, ("b1100111".U)(7.W))
    def LW      = Cat(lwImm, RD_RS1, ("b010".U)(3.W), RS2, ("b0000011".U)(7.W))
    def ADDI    = Cat(iImm, RD_RS1, ("b000".U)(3.W), RD_RS1, "b0010011".U)
    def ANDI    = Cat(iImm, RD_RS1, ("b111".U)(3.W), RD_RS1, "b0010011".U)
    def ADD     = Cat("b0000000".U, RS2, RD_RS1, ("b000".U)(3.W), RD_RS1, ("b0110011".U)(7.W))
    def AND     = Cat("b0000000".U, RS2, RD_RS1, "b111".U, RD_RS1, ("b0110011".U)(7.W))
    def OR      = Cat("b0000000".U, RS2, RD_RS1, "b110".U, RD_RS1, ("b0110011".U)(7.W))
    def XOR     = Cat("b0000000".U, RS2, RD_RS1, "b100".U, RD_RS1, ("b0110011".U)(7.W))
    def SUB     = Cat("b0100000".U, RS2, RD_RS1, ("b000".U)(3.W), RD_RS1, ("b0110011".U)(7.W))
    def MV     = Cat("b0000000".U, RS2, RD_RS1, ("b000".U)(3.W), RD_RS1, ("b0110011".U)(7.W))
    def NOP     = "h00000013".U
    def EBREAK  = "h00100073".U
    def ILLEGAL = 0.U

    val cases = Array(
        (io.instIn(15,0) === CLW)           -> LW,
        // (io.instIn(15,0) === CLWSP) -> ,
        (io.instIn(15,0) === CSW)           -> SW,
        // (io.instIn(15,0) === CSWSP) -> ,        
        (io.instIn(15,0) === CJ)            -> JAL,
        (io.instIn(15,0) === CJAL)          -> JAL,
        (io.instIn(15,0) === CJR)           -> JALR,
        (io.instIn(15,0) === CJALR)         -> JALR,
        (io.instIn(15,0) === CBEQZ)         -> BEQZ,
        (io.instIn(15,0) === CBNEQZ)        -> BNEZ,
        (io.instIn(15,0) === CLI)           -> ADDI,
        (io.instIn(15,0) === CLUI)          -> LUI,
        (io.instIn(15,0) === CADDI)         -> ADDI,
        // (io.instIn(15,0) === CADDI16SP) -> ,
        // (io.instIn(15,0) === CADDI4SPN) -> ,
        (io.instIn(15,0) === CSLLI)         -> SLLI,
        (io.instIn(15,0) === CSRLI)         -> SRLI,
        (io.instIn(15,0) === CSRAI)         -> SRAI,
        (io.instIn(15,0) === CANDI)         -> ANDI,
        (io.instIn(15,0) === CMV)           -> MV,
        (io.instIn(15,0) === CADD)          -> ADD,
        (io.instIn(15,0) === CAND)          -> AND,
        (io.instIn(15,0) === COR)           -> OR,
        (io.instIn(15,0) === CXOR)          -> XOR,
        (io.instIn(15,0) === CSUB)          -> SUB,
        (io.instIn(15,0) === CNOP)          -> NOP,
        (io.instIn(15,0) === CEBREAK)       -> EBREAK,
        (io.instIn(15,0) === CILLEGAL)      -> ILLEGAL)

    io.instOut := MuxCase(io.instIn, cases)
    io.compressed := io.instIn =/= io.instOut

}