package co.upvest.mberndt

import com.thoughtworks.binding.Binding.{BindingSeq, MultiMountPoint}

import scala.collection.GenSeq
abstract class StatefulMultiMountPoint[A](as: BindingSeq[A]) extends MultiMountPoint[A](as) {
  protected type State
  private var state: State = _
  protected def init(): State
  protected def destroy(s: State): Unit
  protected def set(state: State, children: Seq[A]): Unit
  protected def splice(state: State, from: Int, that: GenSeq[A], replaced: Int): Unit

  final override protected def set(children: Seq[A]): Unit =
    set(state, children)

  final override protected def splice(from: Int, that: GenSeq[A], replaced: Int): Unit =
    splice(state, from, that, replaced)

  final override protected def mount(): Unit = {
    state = init()
    super.mount()
  }

  final override protected def unmount(): Unit = {
    super.unmount()
    destroy(state)
  }
}
