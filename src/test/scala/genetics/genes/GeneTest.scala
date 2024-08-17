package cl.ravenhill.plascevo
package genetics.genes

class GeneTest extends AbstractPlascevoTest {
    "A Gene" - {
        "can be mutated" in {
            forAll(simpleGeneGen()) { gene =>
                val mutatedGene = gene.mutate()
                mutatedGene.value should be(gene.value + 1)
            }
        }

        "can be flattened" in {
            forAll(simpleGeneGen()) { gene =>
                gene.flatten() should be(Seq(gene.value))
            }
        }

        "can be folded to the right" in {
            forAll(simpleGeneGen()) { gene =>
                gene.foldRight(10)(_ + _) should be(gene.value + 10)
            }
        }

        "can be folded to the left" in {
            forAll(simpleGeneGen()) { gene =>
                gene.foldLeft(10)(_ + _) should be(10 + gene.value)
            }
        }
    }
}

