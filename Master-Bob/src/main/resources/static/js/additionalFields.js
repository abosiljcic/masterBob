document.addEventListener("DOMContentLoaded", function() {
    var accountType = document.getElementById("role");
    var additionalFields = document.getElementById("additionalFields");
    var addressInput = document.getElementById("address");
    var categoryInput = document.getElementById("category");
    var postCodeInput = document.getElementById("postCode");

    additionalFields.style.display = "none"

    accountType.addEventListener("change", function() {
        if (accountType.value === "contractor") {
            additionalFields.style.display = "block";
            addressInput.required = true;
            categoryInput.required = true;
            postCodeInput.requried = true;
        } else {
            additionalFields.style.display = "none";
            addressInput.required = false;
            categoryInput.required = false;
            postCodeInput.requried = false;
        }
    });
});
