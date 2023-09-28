import logic.evaluateExpression
import org.junit.Test
import kotlin.test.DefaultAsserter.assertEquals
import kotlin.test.DefaultAsserter.assertTrue
import kotlin.test.DefaultAsserter.fail


class Test{
    @Test
    fun testExpression(){
        assertEquals("ÎÞ·ûºÅ×´Ì¬",evaluateExpression("0.0").toString(),(0.0).toString())
    }

    @Test
    fun testExpressionOperandIsMissingAboutMultiplication(){
        listOf(
            "*",
            "1*",
            "*2"
        ).forEach {
            try {
                evaluateExpression(it)
                fail("Expected an IndexOutOfBoundsException to be thrown")
            }catch (
                e:Exception
            ){
                assertTrue("ÎÞ·ûºÅ×´Ì¬",e is IllegalArgumentException)
            }
        }
    }

    @Test
    fun testExpressionOperandIsMissingDiv(){
        listOf(
            "/",
            "1/",
            "/2"
        ).forEach {
            try {
                evaluateExpression(it)
                fail("Expected an IndexOutOfBoundsException to be thrown")
            }catch (
                e:Exception
            ){
                assertTrue("ÎÞ·ûºÅ×´Ì¬",e is IllegalArgumentException)
            }
        }
    }

    @Test
    fun testExpressionOperandIsMissingAdd(){
        listOf(
            "+",
            "1+",
            "+2"
        ).forEach {
            try {
                evaluateExpression(it)
                fail("Expected an IndexOutOfBoundsException to be thrown")
            }catch (
                e:Exception
            ){
                assertTrue("ÎÞ·ûºÅ×´Ì¬",e is IllegalArgumentException)
            }
        }
    }

    fun testExpressionOperandIsMissingSubstruction(){
        listOf(
            "-",
            "1-",
            "-2"
        ).forEach {
            try {
                evaluateExpression(it)
                fail("Expected an IndexOutOfBoundsException to be thrown")
            }catch (
                e:Exception
            ){
                assertTrue("ÎÞ·ûºÅ×´Ì¬",e is IllegalArgumentException)
            }
        }
    }
}