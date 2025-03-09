document.addEventListener("DOMContentLoaded", function () {
    let courseCodeElement = document.getElementById("courseCode");
    let subjectDropdown = document.getElementById("subjectCode");
    let syllabusLink = document.getElementById("syllabusLink");
    let noSyllabusText = document.getElementById("noSyllabusText");

    if (courseCodeElement && subjectDropdown) {
        courseCodeElement.addEventListener("change", function () {
            let courseId = this.value;

            // Clear existing options
            subjectDropdown.innerHTML = '<option value="">Loading...</option>';

            if (courseId) {
                fetch(`https://online.ctestservices.com/gtuqp/subjects/${courseId}`)
                    .then(response => response.json())
                    .then(data => {
                        subjectDropdown.innerHTML = '<option value="">Select a Subject</option>'; // Reset dropdown

                        data.forEach(subject => {
                            let option = document.createElement("option");
                            option.value = subject.id;  // Use Subject ID as value
                            option.textContent = `${subject.subjectCode} - ${subject.subject_name}`;  // Display Code & Name
                            option.setAttribute("data-syllabus", subject.syllabus || ""); // Set syllabus URL
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
                    syllabusLink.href = "../" + syllabusUrl;
                    syllabusLink.style.display = "inline"; // Show link
                    noSyllabusText.style.display = "none"; // Hide "No syllabus available" text
                } else {
                    syllabusLink.style.display = "none"; // Hide link
                    noSyllabusText.style.display = "inline"; // Show "No syllabus available" text
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
