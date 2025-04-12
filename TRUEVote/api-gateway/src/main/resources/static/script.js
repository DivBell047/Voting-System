document.addEventListener('DOMContentLoaded', () => {
    // --- Configuration ---
    // URLs based on API Gateway (port 9090) and specific Controller paths
    const API_BASE_URL = ''; // Relative paths work as HTML is served by the gateway

    // Corrected URLs based on your Controller mappings
    const CANDIDATES_API_URL = `${API_BASE_URL}/candidates/`;        // GET /candidates/  AND POST /candidates/
    const VOTES_API_URL = `${API_BASE_URL}/registration/register`; // POST /registration/register
    const RESULTS_API_URL = `${API_BASE_URL}/counting/rankings`;   // GET /counting/rankings

    // --- DOM Elements ---
    const candidatesListDiv = document.getElementById('candidates-list');
    const candidateSelect = document.getElementById('candidate-select');
    const resultsListDiv = document.getElementById('results-list');
    const refreshCandidatesButton = document.getElementById('refresh-candidates');
    const refreshResultsButton = document.getElementById('refresh-results');
    const castVoteButton = document.getElementById('cast-vote-button');
    const voterIdInput = document.getElementById('voter-id');
    const voteStatusP = document.getElementById('vote-status');

    // --- Add Candidate Form Elements ---
    const addCandidateForm = document.getElementById('add-candidate-form');
    const newCandidateNameInput = document.getElementById('new-candidate-name');
    const newCandidatePartyInput = document.getElementById('new-candidate-party');
    const newCandidatePositionInput = document.getElementById('new-candidate-position');
    const newCandidateDescriptionInput = document.getElementById('new-candidate-description');
    const newCandidateImageUrlInput = document.getElementById('new-candidate-image-url');
    const addCandidateButton = document.getElementById('add-candidate-button');
    const addCandidateStatusP = document.getElementById('add-candidate-status');

    // --- State ---
    // Store candidate names mapped by ID for easy lookup in results
    let candidateMap = new Map();

    // --- Utility Functions ---
    function setStatusMessage(element, message, type) {
        element.textContent = message;
        if (type === 'error') {
            element.style.color = 'red';
        } else if (type === 'success') {
            element.style.color = 'green';
        } else if (type === 'pending') {
            element.style.color = 'orange';
        } else {
            element.style.color = 'black'; // Default or info
        }
    }

    // --- Core Functions ---

    // Fetch and display candidates
    async function fetchCandidates() {
        candidatesListDiv.innerHTML = '<p>Loading candidates...</p>';
        // Clear dropdown but keep the default "--Select..." option
        candidateSelect.length = 1; // Remove all options except the first one
        candidateMap.clear(); // Clear previous map

        try {
            console.log(`Fetching candidates from: ${CANDIDATES_API_URL}`);
            const response = await fetch(CANDIDATES_API_URL); // GET /candidates/
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const candidates = await response.json(); // Expecting List<CandidateDTO>

            if (!candidates || candidates.length === 0) {
                candidatesListDiv.innerHTML = '<p>No candidates found.</p>';
                return;
            }

            // Populate the list display and the dropdown
            const listItems = [];
            candidates.forEach(c => {
                const displayName = c.name || `Candidate ID ${c.id}`;
                // Store for results lookup
                candidateMap.set(c.id, displayName);

                // Add to display list
                listItems.push(`<li>${displayName} (Party: ${c.party || 'N/A'}, ID: ${c.id})</li>`);

                // Add to dropdown
                const option = document.createElement('option');
                option.value = c.id; // CandidateDTO has 'id'
                option.textContent = displayName; // CandidateDTO has 'name'
                candidateSelect.appendChild(option);
            });
            candidatesListDiv.innerHTML = `<ul>${listItems.join('')}</ul>`;

        } catch (error) {
            console.error('Error fetching candidates:', error);
            candidatesListDiv.innerHTML = `<p style="color: red;">Error loading candidates. Check console and ensure services are running.</p>`;
        }
    }

    // Fetch and display results (rankings)
    async function fetchResults() {
        resultsListDiv.innerHTML = '<p>Loading results...</p>';

        // Ensure candidates are loaded first to map IDs to names
        if (candidateMap.size === 0) {
             console.log("Candidate map empty, attempting to fetch candidates before results...");
             await fetchCandidates(); // Make sure map is populated
             if (candidateMap.size === 0) {
                 resultsListDiv.innerHTML = '<p style="color: orange;">Cannot fetch results until candidates are loaded. Try refreshing candidates.</p>';
                 return;
             }
        }

        try {
            console.log(`Fetching results from: ${RESULTS_API_URL}`);
            const response = await fetch(RESULTS_API_URL); // GET /counting/rankings
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            // Expecting List<VoteResultRanking> -> [{ candidateId: ..., voteCount: ... }, ...]
            const results = await response.json();

            if (!results || results.length === 0) {
                resultsListDiv.innerHTML = '<p>No results available yet.</p>';
                return;
            }

            // Sort results by voteCount descending (optional, backend might already do this)
            results.sort((a, b) => (b.voteCount || 0) - (a.voteCount || 0));

            // Map results to display strings using the candidateMap
            const resultItems = results.map(r => {
                const candidateName = candidateMap.get(r.candidateId) || `Unknown Candidate (ID: ${r.candidateId})`;
                const voteCount = r.voteCount !== null && r.voteCount !== undefined ? r.voteCount : 0;
                return `<li>${candidateName}: ${voteCount} votes</li>`;
            });

            resultsListDiv.innerHTML = `<ul>${resultItems.join('')}</ul>`;

        } catch (error) {
            console.error('Error fetching results:', error);
            resultsListDiv.innerHTML = `<p style="color: red;">Error loading results. Check console and ensure services are running.</p>`;
        }
    }

    // Cast a vote
    async function castVote() {
        const selectedCandidateIdStr = candidateSelect.value;
        const voterIdStr = voterIdInput.value.trim();
        setStatusMessage(voteStatusP, '', null); // Clear previous status

        // --- Input Validation ---
        if (!voterIdStr) {
            setStatusMessage(voteStatusP, 'Error: Please enter your Voter ID.', 'error');
            return;
        }
        if (!selectedCandidateIdStr) {
            setStatusMessage(voteStatusP, 'Error: Please select a candidate.', 'error');
            return;
        }

        const voterId = parseInt(voterIdStr, 10);
        const candidateId = parseInt(selectedCandidateIdStr, 10);

        if (isNaN(voterId)) {
             setStatusMessage(voteStatusP, 'Error: Voter ID must be a number.', 'error');
             return;
        }
        if (isNaN(candidateId)) {
             setStatusMessage(voteStatusP, 'Error: Invalid candidate selected.', 'error'); // Should not happen with dropdown
             return;
        }
        // --- End Validation ---

        // Construct the request body based on VoteController expecting {userId, candidateId}
        const voteData = {
            userId: voterId,
            candidateId: candidateId
        };

        try {
            castVoteButton.disabled = true;
            setStatusMessage(voteStatusP, 'Submitting vote...', 'pending');

            console.log(`Casting vote to: ${VOTES_API_URL} with data:`, JSON.stringify(voteData));
            const response = await fetch(VOTES_API_URL, { // POST /registration/register
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(voteData),
            });

            if (response.status === 201) { // Check for 201 Created status
                setStatusMessage(voteStatusP, 'Vote cast successfully!', 'success');
                voterIdInput.value = ''; // Clear input
                candidateSelect.value = ''; // Reset dropdown
                fetchResults(); // Refresh results view automatically
            } else {
                 // Try to get error message from response body (expected for 400 Bad Request)
                 let errorMsg = `Vote failed. Status: ${response.status}`;
                 try {
                    const errorText = await response.text(); // Get text body for error message
                    if(errorText) {
                        // Avoid showing overly technical messages directly if possible
                        errorMsg = errorText.includes("already voted")
                            ? "Error: You have already voted."
                            : `Error: ${errorText}`;
                    }
                 } catch (e) { /* Ignore if body is empty or not text */ }
                throw new Error(errorMsg);
            }

        } catch (error) {
            console.error('Error casting vote:', error);
            setStatusMessage(voteStatusP, error.message, 'error'); // Display specific error
        } finally {
            castVoteButton.disabled = false; // Re-enable button
        }
    }

    // Add a new candidate
    async function addCandidate(event) {
        event.preventDefault(); // Prevent default form submission page reload
        setStatusMessage(addCandidateStatusP, '', null); // Clear previous status

        const name = newCandidateNameInput.value.trim();
        const party = newCandidatePartyInput.value.trim();
        const position = newCandidatePositionInput.value.trim();
        const description = newCandidateDescriptionInput.value.trim();
        const imageUrl = newCandidateImageUrlInput.value.trim();

        // Basic client-side validation
        if (!name) {
            setStatusMessage(addCandidateStatusP, 'Error: Candidate Name is required.', 'error');
            return;
        }

        // Construct the DTO object matching CandidateCreateDTO
        const candidateData = {
            name: name,
            // Only include optional fields if they have a value
            ...(party && { party: party }),
            ...(position && { position: position }),
            ...(description && { description: description }),
            ...(imageUrl && { imageUrl: imageUrl })
        };

        try {
            addCandidateButton.disabled = true;
            setStatusMessage(addCandidateStatusP, 'Adding candidate...', 'pending');

            console.log(`Adding candidate to: ${CANDIDATES_API_URL} with data:`, JSON.stringify(candidateData));

            const response = await fetch(CANDIDATES_API_URL, { // POST /candidates/
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(candidateData)
            });

            if (response.status === 201) { // Check for 201 Created
                const newCandidate = await response.json(); // Get the created candidate details
                setStatusMessage(addCandidateStatusP, `Candidate '${newCandidate.name}' added successfully!`, 'success');

                // Clear the form
                addCandidateForm.reset();

                // Refresh the candidate list to show the new candidate
                await fetchCandidates(); // Reload candidates list and dropdown

            } else {
                // Try to get error details (e.g., validation errors)
                let errorMsg = `Failed to add candidate. Status: ${response.status}`;
                try {
                    const errorData = await response.json();
                    // Try to parse common Spring validation error structures
                    if (errorData.errors && Array.isArray(errorData.errors)) {
                         errorMsg = `Error: ${errorData.errors.map(e => e.defaultMessage || `${e.field}: ${e.code}`).join(', ')}`;
                    } else if (errorData.message) {
                         errorMsg = `Error: ${errorData.message}`;
                    } else if (errorData.error) {
                         errorMsg = `Error: ${errorData.error}`;
                    } else {
                         const textError = await response.text(); // Fallback for non-JSON errors
                         if (textError) errorMsg = `Error: ${textError}`;
                    }
                } catch (e) { /* Ignore if response body isn't JSON or parsing fails */ }
                throw new Error(errorMsg);
            }

        } catch (error) {
            console.error('Error adding candidate:', error);
            setStatusMessage(addCandidateStatusP, error.message, 'error'); // Show specific error
        } finally {
            addCandidateButton.disabled = false; // Re-enable button
        }
    }

    // --- Event Listeners ---
    refreshCandidatesButton.addEventListener('click', async () => {
        setStatusMessage(voteStatusP, '', null); // Clear vote status on refresh
        setStatusMessage(addCandidateStatusP, '', null); // Clear add candidate status
        await fetchCandidates();
        // Results depend on candidates, so refresh them too
        await fetchResults();
    });

    refreshResultsButton.addEventListener('click', () => {
         setStatusMessage(voteStatusP, '', null); // Clear vote status on refresh
         fetchResults();
    });

    castVoteButton.addEventListener('click', castVote);
    addCandidateForm.addEventListener('submit', addCandidate); // Listen for form submission

    // --- Initial Load ---
    async function initialLoad() {
        await fetchCandidates(); // Load candidates first
        await fetchResults();    // Then load results (which might depend on candidate map)
    }
    initialLoad(); // Run the initial load sequence

});