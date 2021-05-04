/*
 * The MIT License
 *
 * Copyright (c) 2021 Squeng AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import api.EventsController
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.scalatest.funsuite.AnyFunSuite

class DependencyRulesTestSuite extends AnyFunSuite {

  test("ArchUnit PoC") {
    val classes =
      new ClassFileImporter().importPackagesOf(classOf[EventsController])

    noClasses()
      .that()
      .resideInAPackage("api..")
      .should()
      .dependOnClassesThat()
      .resideOutsideOfPackages(
        "java..",
        "javax..",
        "scala..",
        "play.api..",
        "play.core..",
        "play.filters..",
        "router..",
        "com.google.i18n.phonenumbers..",
        "api..",
        "domain.spi..",
        "domain.value_objects.."
      )
      .check(classes)
  }
}
