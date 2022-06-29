package digitalCircuit.src.fifo

//CDC的关键：数据的值本身，数量，顺序都不能错
//FIFO的作用:multi-Bit CDC
//FIFO设计的关键：产生可靠的FIFO读写指针和生成FIFO“空”/“满”状态标志。
//gradcode的作用：只有一位变化，用来跨时钟判断空满
import chisel3._
import chisel3.RegNext
class ReadBundle(width: Int) extends Bundle{
  val clk = Input(Clock())
  val reset = Input(Bool())
  val data = Input(UInt(width.W))
  val enable = Input(Bool())
  val empty = Output(Bool())
}
class WriteBundle(width: Int) extends Bundle{
  val clk = Input(Clock())
  val reset = Input(Bool())
  val data = Output(UInt(width.W))
  val enable = Input(Bool())
  val full = Output(Bool())
}



class FIFO(width: Int, lengthLog2: Int) extends RawModule{
  val read = new ReadBundle(width)
  val write = new WriteBundle(width)

  def gradCode(x: UInt): UInt ={
    x ^ (x >> 1).asUInt
  }

  // 统一reset信号
  val sysReset = Input(Bool())

  val addrWriteNext = Wire(UInt(1 << (lengthLog2 + 1)))
  val addrRead = Wire(UInt(1 << (lengthLog2 + 1)))

  val WriteGradCode = Wire(UInt(1 << (lengthLog2 + 1)))
  val readGradCode = Wire(UInt(1 << (lengthLog2 + 1)))

  val write2readGrad = Wire(UInt(1 << (lengthLog2 +1)))
  val read2writeGrad = Wire(UInt(1 << (lengthLog2 +1)))


  val ram = SyncReadMem(1 << lengthLog2, UInt(width.W))   // 2^depth

  // state
  withClockAndReset(write.clk, write.reset) {
    val addrWrite = RegNext(addrWriteNext, 0.U)
    val cdcRegWrite1 = RegNext(addrWriteNext, 0.U)
    val cdcRegWrite2 = RegNext(addrWriteNext, 0.U)

    write2readGrad := cdcRegWrite2

  }

  withClockAndReset(read.clk, read.reset) {
    val addrRead = RegNext()
    val cdcRegRead1 = RegEnable()
    val cdcRegRead2 = RegEnable()
    write2readGrad := cdcRegWrite2
  }






}