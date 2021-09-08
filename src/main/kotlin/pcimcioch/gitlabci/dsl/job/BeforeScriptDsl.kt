package pcimcioch.gitlabci.dsl.job

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import pcimcioch.gitlabci.dsl.DslBase
import pcimcioch.gitlabci.dsl.serializer.ValueSerializer

@Serializable(with = BeforeScriptDsl.BeforeScriptDslSerializer::class)
class BeforeScriptDsl : DslBase() {
    var commands: MutableList<String> = mutableListOf()

    fun exec(command: String) = commands.add(command)
    operator fun String.unaryPlus() = this@BeforeScriptDsl.commands.add(this)

    object BeforeScriptDslSerializer : ValueSerializer<BeforeScriptDsl, List<String>>(ListSerializer(String.serializer()), BeforeScriptDsl::commands)
    companion object {
        init {
            addSerializer(BeforeScriptDsl::class, serializer())
        }
    }
}

fun createBeforeScript(block: BeforeScriptDsl.() -> Unit = {}) = BeforeScriptDsl().apply(block)
fun createBeforeScript(vararg elements: String, block: BeforeScriptDsl.() -> Unit = {}) = createBeforeScript(elements.toList(), block)
fun createBeforeScript(elements: Iterable<String>, block: BeforeScriptDsl.() -> Unit = {}) = BeforeScriptDsl().apply { elements.forEach { exec(it) } }.apply(block)
