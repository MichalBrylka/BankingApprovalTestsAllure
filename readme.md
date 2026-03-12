# Running Tests and Generating Allure Reports

This guide explains how to:

1. Run Maven tests
2. Serve an interactive Allure report
3. Generate a standalone single-file report

---

# Prerequisites

Make sure the following tools are installed:

* **Java**
* **Maven**
* **Allure CLI**

### Install Allure CLI

**NPM**

```bash
npm install -g allure-commandline --save-dev
```
**Verify installation**

```bash
allure --version
```

---

# 1. Run Maven Tests

Execute the tests with Maven:

```bash
mvn clean test
```

After tests finish, Allure result files will be generated in (specified in [allure.properties](./src/test/resources/allure.properties)):

```
target/allure-results
```

These files contain raw execution data used to build the report.

---

# 2. Serve the Allure Report (Interactive)

To generate and open the report locally:

```bash
allure serve target/allure-results
```

What happens:

1. A temporary report is generated
2. A local web server starts
3. The report automatically opens in your browser

This is the **fastest way to inspect results during development**.

---

# 3. Generate a Static HTML Report

If you want a persistent report directory:

```bash
allure generate target/allure-results --clean -o target/allure-report
```

The report will be created in:

```
target/allure-report
```

Open it manually:

```bash
allure open target/allure-report
```

Or simply open:

```
target/allure-report/index.html
```

---

# 4. Generate a Single-File Report

Sometimes you may want a **portable report** (for email, CI artifacts, or sharing).

Generate a single HTML file:

```bash
allure generate target/allure-results --clean --single-file -o target/allure-single
```

This produces:

```
target/allure-single/index.html
```

This file contains the **entire report bundled into one HTML file** and can be shared without additional assets.

---

# Typical Workflow

```bash
# Run tests
mvn clean test

# Quickly inspect results
allure serve target/allure-results

# Or generate a static report
allure generate target/allure-results --clean -o target/allure-report
allure open target/allure-report
```

---

# Project Structure Example

```
project
├─ src
│  └─ test
│     └─ resources
│        └─ allure.properties
├─ target
│  ├─ allure-results
│  ├─ allure-report
│  └─ allure-single
└─ pom.xml
```

---

# CI Tip

In CI pipelines:

1. Run tests
2. Archive `target/allure-results`
3. Generate reports as a separate step

Example:

```bash
mvn test
allure generate target/allure-results --clean -o target/allure-report
```

The `target/allure-report` directory can then be published as a pipeline artifact.
