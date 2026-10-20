# HouseMate — private development repository

**Status:** Work in progress | **Visibility:** Private — course-related source material, not authorized for public distribution.

This repository tracks the evolving Java implementation and original design work for HouseMate, developed during Harvard Extension School CSCI E-97. Do not make this repository public or distribute its assignment solutions or course-derived materials without first confirming the instructor's written permission and applicable academic-integrity requirements.

## Implementation status

| Component | Status | What is here |
| --- | --- | --- |
| Knowledge Graph | Implemented (Assignment 1) | Node/predicate/triple storage, import, query engine |
| HouseMate Model Service | Implemented (Assignment 2) | Houses, rooms, occupants, sensors, appliances, model service, command-line interface, energy-use calculations, error handling |
| HouseMate Controller Service | **In progress** (Assignment 3) | Design diagrams only; Java controller classes not implemented in this snapshot |

The Java implementation under `cscie97/` was copied **without modifying its source** from the most recent cumulative project snapshot. The earliest submitted versions are preserved separately in the original assignment archive, not duplicated within this working source tree.

## Repository structure

```text
cscie97/
  asn1/knowledge/engine/         Knowledge Graph source
  asn1/test/                     Student-authored Knowledge Graph drivers
  asn2/housemate/model/          HouseMate Model Service source
  asn2/housemate/test/           Model Service script driver
  asn3/housemate/controller/     Reserved for upcoming Controller source
examples/                         Student-authored functional/error test scripts
docs/model-service/               Student-authored diagrams and design/results reports
docs/controller-design/           In-progress Controller UML diagrams and editable design
.gitignore
README.md
```

## Build (Windows PowerShell)

The original coursework used **JDK 25**. The included Java files also compiled successfully in a local OpenJDK 21 check; that does not replace running tests on your Windows/JDK 25 setup.

From the repository root:

```powershell
New-Item -ItemType Directory -Force .\build | Out-Null
$javaFiles = Get-ChildItem .\cscie97 -Recurse -File -Filter '*.java' | Select-Object -ExpandProperty FullName
javac -d .\build $javaFiles
```

## Run the implemented Model Service

```powershell
java -cp .\build cscie97.asn2.housemate.test.TestDriver .\examples\housesetup_good_tests.txt
java -cp .\build cscie97.asn2.housemate.test.TestDriver .\examples\housesetup_bad_tests.txt
```

The second script is intentionally designed to report invalid operations while continuing through the remaining test cases. These are script-based functional demonstrations, not a comprehensive automated unit-test suite.

The Controller Service is not yet implemented or runnable. Do not claim the Observer/Command implementations or controller functionality are complete based only on the planning diagrams.

## Included / excluded material

Included: latest cumulative Java files, original student-authored examples, and original student-authored design artifacts.

Excluded intentionally: duplicated Java snapshots, nested submission ZIPs, compiled outputs, backup files, Windows metadata, supplied reference documents/rubrics, provided sample `housesetup.txt`, and Google Docs `.gdoc` link stubs. The original uploaded archive remains the source of record for all those materials.

**Important:** This is a development repository, not a public recruiter portfolio link. A separately developed project or instructor-approved publication would be necessary for a publicly reviewable code sample.
