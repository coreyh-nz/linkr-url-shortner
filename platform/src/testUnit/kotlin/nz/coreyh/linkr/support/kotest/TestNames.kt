package nz.coreyh.linkr.support.kotest

import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.test.TestScope
import io.kotest.datatest.withTests

/**
 * Kotest rejects blank test names; use as a withData/withTests nameFn for
 * string inputs that may be blank.
 */
fun String.orPlaceholder(placeholder: String = "<empty>"): String = ifBlank { placeholder }

/**
 * [withTests] for string inputs, defaulting the nameFn to [orPlaceholder]
 * so blank cases don't need one.
 */
suspend fun FunSpecContainerScope.withStringTests(
    cases: Iterable<String>,
    test: suspend TestScope.(String) -> Unit,
) = withTests({ it.orPlaceholder() }, cases, test)
