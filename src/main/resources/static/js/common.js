document.addEventListener("DOMContentLoaded", function () {
    let courseCodeElement = document.getElementById("courseCode");
    let subjectDropdown = document.getElementById("subjectCode");
    let syllabusLink = document.getElementById("syllabusLink");
    let noSyllabusText = document.getElementById("noSyllabusText");
    let yearSemSelect = document.getElementById("yearsem");
    let subjectSelect = document.getElementById("subject");

    if (courseCodeElement && subjectDropdown) {
        courseCodeElement.addEventListener("change", function () {
            let courseId = this.value;
            subjectDropdown.innerHTML = '<option value="">Loading...</option>';

            if (courseId) {
                fetch(`https://online.ctestservices.com/gqpsecon/subjects/${courseId}`)
                    .then(response => response.json())
                    .then(data => {
                        subjectDropdown.innerHTML = '<option value="">Select a Subject</option>';
                        data.forEach(subject => {
                            let option = new Option(
                                `${subject.subjectCode} - ${subject.subject_name}`,
                                subject.id
                            );
                            option.setAttribute("data-syllabus", subject.syllabus || "");
                            subjectDropdown.appendChild(option);
                        });
                    })
                    .catch(error => {
                        console.error("Error fetching subjects:", error);
                        subjectDropdown.innerHTML = '<option value="">Error Loading Subjects</option>';
                    });
            } else {
                subjectDropdown.innerHTML = '<option value="">Select a Course First</option>';
            }
        });
    }

    if (subjectDropdown) {
        subjectDropdown.addEventListener("change", function () {
            let selectedOption = subjectDropdown.options[subjectDropdown.selectedIndex];
            let syllabusUrl = selectedOption.getAttribute("data-syllabus");

            if (syllabusLink && noSyllabusText) {
                if (syllabusUrl) {
                    syllabusLink.href = `../${syllabusUrl}`;
                    syllabusLink.style.display = "inline";
                    noSyllabusText.style.display = "none";
                } else {
                    syllabusLink.style.display = "none";
                    noSyllabusText.style.display = "inline";
                }
            }
        });
    }

    // Sidebar Toggle
    let toggleButton = document.querySelector(".toggle-btn");
    let sidebar = document.querySelector("#sidebar");

    if (toggleButton && sidebar) {
        toggleButton.addEventListener("click", function () {
            sidebar.classList.toggle("expand");
        });
    }
});

// Function to fetch Semesters & Subjects
function fetchData() {
    let courseId = document.getElementById("courseCode").value;
    let yearSemSelect = document.getElementById("yearsem");

    // Reset dropdowns
    yearSemSelect.innerHTML = '<option value="All">All</option>';

    if (!courseId || courseId === "All") return;

    fetch(`https://online.ctestservices.com/gqpsecon/getSemestersAndSubjects?courseId=${courseId}`)
        .then(response => response.json())
        .then(data => {
            data.forEach(sem => {
                yearSemSelect.appendChild(new Option(sem, sem));
            });
        })
        .catch(error => console.error("Error fetching data:", error));
}

// Function to fetch subjects based on semester selection
function fetchSubjectsData() {
    let courseId = document.getElementById("courseCode").value;
    let semester = document.getElementById("yearsem").value;
    let subjectSelect = document.getElementById("subject");

    // Reset subject dropdown
    subjectSelect.innerHTML = '<option value="All">All</option>';

    if (!courseId || courseId === "All" || !semester || semester === "All") return;

    fetch(`https://online.ctestservices.com/gqpsecon/getSubjectsByCourseAndSemester?courseId=${courseId}&semester=${semester}`)
        .then(response => response.json())
        .then(data => {
            data.forEach(sub => {
                subjectSelect.appendChild(new Option(`${sub.subjectCode} - ${sub.subject_name}`, sub.id));
            });
        })
        .catch(error => console.error("Error fetching data:", error));
}
