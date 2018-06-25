package evaluation

import furniture.model.Sofa
import furniture.model.TV

class EvaluatorTest {

//    class TestBoundaries(
//            override val left: Double = 0.0,
//            override val right: Double = 0.0,
//            override val top: Double = 0.0,
//            override val bottom: Double = 0.0
//    ) : Boundaries
//
//    private lateinit var evaluator: Evaluator
//
//    @Before
//    fun setUp() {
//        evaluator = Evaluator()
//    }
//
//    // Is Furniture Overlap
//
//    @Test
//    fun `is furniture no overlap right smaller`() {
//        val one = TestBoundaries(2.0, 4.0, 1.0, 4.0)
//        val two = TestBoundaries(6.0, 8.0, 2.0, 3.0)
//
//        assertFalse(evaluator.isFurnitureOverlap(one, two))
//        assertFalse(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    @Test
//    fun `is furniture no overlap left smaller`() {
//        val one = TestBoundaries(1.0, 2.0, 2.0, 3.0)
//        val two = TestBoundaries(3.0, 4.0, 1.0, 4.0)
//
//        assertFalse(evaluator.isFurnitureOverlap(one, two))
//        assertFalse(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    @Test
//    fun `is furniture no overlap top smaller`() {
//        val one = TestBoundaries(1.0, 4.0, 1.0, 2.0)
//        val two = TestBoundaries(2.0, 3.0, 3.0, 4.0)
//
//        assertFalse(evaluator.isFurnitureOverlap(one, two))
//        assertFalse(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    @Test
//    fun `is furniture no overlap bottom smaller`() {
//        val one = TestBoundaries(1.0, 4.0, 3.0, 4.0)
//        val two = TestBoundaries(2.0, 3.0, 1.0, 2.0)
//
//        assertFalse(evaluator.isFurnitureOverlap(one, two))
//        assertFalse(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    @Test
//    fun `is furniture overlap right upper corner`() {
//        val one = TestBoundaries(1.0, 3.0, 3.0, 1.0)
//        val two = TestBoundaries(2.0, 4.0, 4.0, 2.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//    }
//
//    @Test
//    fun `is furniture overlap left upper corner`() {
//        val one = TestBoundaries(2.0, 4.0, 3.0, 1.0)
//        val two = TestBoundaries(1.0, 3.0, 4.0, 2.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//    }
//
//    @Test
//    fun `is furniture overlap right bottom corner`() {
//        val one = TestBoundaries(1.0, 3.0, 4.0, 2.0)
//        val two = TestBoundaries(2.0, 4.0, 3.0, 1.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//    }
//
//    @Test
//    fun `is furniture overlap left bottom corner`() {
//        val one = TestBoundaries(2.0, 4.0, 4.0, 2.0)
//        val two = TestBoundaries(1.0, 3.0, 3.0, 1.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//    }
//
//    @Test
//    fun `is furniture overlap is inside`() {
//        val one = TestBoundaries(2.0, 3.0, 2.0, 3.0)
//        val two = TestBoundaries(1.0, 4.0, 4.0, 1.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//        assertTrue(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    @Test
//    fun `is furniture overlap is covered`() {
//        val one = TestBoundaries(2.0, 4.0, 2.0, 4.0)
//        val two = TestBoundaries(2.0, 4.0, 2.0, 4.0)
//
//        assertTrue(evaluator.isFurnitureOverlap(one, two))
//        assertTrue(evaluator.isFurnitureOverlap(two, one))
//    }
//
//    // Is Furniture In Room
//    //TODO
//
//    // Is Good TVUI Angle
//
//    @Test
//    fun `is good tv angle tv in front of sofa`() {
//        assertTrue(evaluator.isGoodTvAngle(
//                tv = TV(4.0, 10.0),
//                sofa = Sofa(4.0, 4.0))
//        )
//
//        assertTrue(evaluator.isGoodTvAngle(
//                tv = TV(4.0, 4.0),
//                sofa = Sofa(4.0, 10.0))
//        )
//
//        assertTrue(evaluator.isGoodTvAngle(
//                tv = TV(4.0, 4.0),
//                sofa = Sofa(10.0, 4.0))
//        )
//
//        assertTrue(evaluator.isGoodTvAngle(
//                tv = TV(10.0, 4.0),
//                sofa = Sofa(4.0, 4.0))
//        )
//    }
//
//    @Test
//    fun `is good tv angle good angle`() {
//        assertTrue(evaluator.isGoodTvAngle(
//                tv = TV(1.0, 13.0),
//                sofa = Sofa(6.0, 2.0)))
//    }
//
//
//    @Test
//    fun `is good tv angle bad angle`() {
//        assertFalse(evaluator.isGoodTvAngle(
//                tv = TV(1.0, 10.0),
//                sofa = Sofa(6.0, 2.0)))
//    }
}