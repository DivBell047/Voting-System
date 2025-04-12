document.addEventListener('DOMContentLoaded', () => {
    // --- Configuration ---
    // URLs based on API Gateway (port 9090) and specific Controller paths
    const API_BASE_URL = ''; // Relative paths work as HTML is served by the gateway

    // Corrected URLs based on your Controller mappings
    const CANDIDATES_API_URL = `${API_BASE_URL}/candidates`;        // GET /candidates
    const VOTES_API_URL = `${API_BASE_URL}/registration/register`; // POST /registration/register
    const RESULTS_API_URL = `${API_BASE_URL}/counting/rankings`;   // GET /counting/rankings
    const UPDATE_API_URL = `${API_BASE_URL}/counting/update`;

    // --- DOM Elements ---
    const candidatesListDiv = document.getElementById('candidates-list');
    const candidateSelect = document.getElementById('candidate-select');
    const resultsListDiv = document.getElementById('results-list');
    const refreshCandidatesButton = document.getElementById('refresh-candidates');
    const refreshResultsButton = document.getElementById('refresh-results');
    const castVoteButton = document.getElementById('cast-vote-button');
    const voterIdInput = document.getElementById('voter-id');
    const voteStatusP = document.getElementById('vote-status');
    const updateVotesButton = document.getElementById('update-votes-button');
    const addUserButton = document.getElementById('add-user-button');
    const addCandidateButton = document.getElementById('add-candidate-button');

    const userStatusP = document.getElementById('add-user-status');
    const candidateStatusP = document.getElementById('add-candidate-status');

    // --- State ---
    // Store candidate names mapped by ID for easy lookup in results
    let candidateMap = new Map();

    // --- Functions ---

    // Fetch and display candidates
    async function fetchCandidates() {
        candidatesListDiv.innerHTML = '<p>Loading candidates...</p>';
        candidateSelect.innerHTML = '<option value="">--Select a Candidate--</option>'; // Clear existing options
        voteStatusP.textContent = ''; // Clear previous vote status
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
                // Store for results lookup
                candidateMap.set(c.id, c.name || `Candidate ID ${c.id}`); // Use name, fallback to ID

                // Add to display list
                listItems.push(`<li>${c.name || `Candidate ID ${c.id}`} (Party: ${c.party || 'N/A'}, ID: ${c.id})</li>`);

                // Add to dropdown
                const option = document.createElement('option');
                option.value = c.id; // CandidateDTO has 'id'
                option.textContent = c.name || `Candidate ID ${c.id}`; // CandidateDTO has 'name'
                candidateSelect.appendChild(option);
            });
            candidatesListDiv.innerHTML = `<ul>${listItems.join('')}</ul>`;

        } catch (error) {
            console.error('Error fetching candidates:', error);
            candidatesListDiv.innerHTML = `<p style="color: red;">Error loading candidates. Check console and ensure candidate service is running and reachable via gateway at ${CANDIDATES_API_URL}.</p>`;
        }
    }

    async function updateVoteCounts() {
            voteStatusP.textContent = '';
            voteStatusP.style.color = 'orange'; // Default color while updating

            try {
                console.log(`Updating votes at: ${UPDATE_API_URL}`);
                const response = await fetch(`${UPDATE_API_URL}`, { // POST /update
                    method: 'POST',
                });

                if (response.status === 200) { // Check for successful update (200 OK)
                    voteStatusP.textContent = 'Vote counts updated successfully!';
                    voteStatusP.style.color = 'green';
                    // Optionally, refresh results after updating votes
                    fetchResults();
                } else {
                    let errorMsg = `Update failed. Status: ${response.status}`;
                    try {
                        const errorText = await response.text(); // Get error message from response body
                        if (errorText) {
                            errorMsg = `Error: ${errorText}`;
                        }
                    } catch (e) { /* Ignore if body is empty or not text */ }
                    throw new Error(errorMsg);
                }

            } catch (error) {
                console.error('Error updating votes:', error);
                voteStatusP.textContent = error.message;
                voteStatusP.style.color = 'red';
            }
        }

    // Fetch and display results (rankings)
    async function fetchResults() {
        resultsListDiv.innerHTML = '<p>Loading results...</p>';
        voteStatusP.textContent = '';

        // Ensure candidates are loaded first to map IDs to names
        if (candidateMap.size === 0) {
             console.log("Waiting for candidates to load before fetching results...");
             // Optionally disable refresh button until candidates load? Or show message.
             // For simplicity, we'll just try fetching candidates again if needed,
             // though ideally this is handled by initial load order.
             await fetchCandidates(); // Make sure map is populated
             if (candidateMap.size === 0) {
                 resultsListDiv.innerHTML = '<p>Cannot fetch results until candidates are loaded.</p>';
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
            resultsListDiv.innerHTML = `<p style="color: red;">Error loading results. Check console and ensure counting service is running and reachable via gateway at ${RESULTS_API_URL}.</p>`;
        }
    }

    // Cast a vote
    async function castVote() {
        const selectedCandidateIdStr = candidateSelect.value;
        const voterIdStr = voterIdInput.value.trim();
        voteStatusP.textContent = '';
        voteStatusP.style.color = 'red'; // Default to error color

        // --- Input Validation ---
        if (!voterIdStr) {
            voteStatusP.textContent = 'Error: Please enter your Voter ID.';
            return;
        }
        if (!selectedCandidateIdStr) {
            voteStatusP.textContent = 'Error: Please select a candidate.';
            return;
        }

        const voterId = parseInt(voterIdStr, 10);
        const candidateId = parseInt(selectedCandidateIdStr, 10);

        if (isNaN(voterId)) {
             voteStatusP.textContent = 'Error: Voter ID must be a number.';
             return;
        }
        if (isNaN(candidateId)) {
             voteStatusP.textContent = 'Error: Invalid candidate selected.'; // Should not happen with dropdown
             return;
        }
        // --- End Validation ---


        // Construct the request body based on VoteController expecting {userId, candidateId}
        // IMPORTANT: Assumes your 'Vote' entity/DTO has fields named 'userId' and 'candidateId'
        const voteData = {
            userId: voterId,
            candidateId: candidateId
        };

        try {
            castVoteButton.disabled = true;
            voteStatusP.textContent = 'Submitting vote...';
            voteStatusP.style.color = 'orange';

            console.log(`Casting vote to: ${VOTES_API_URL} with data:`, JSON.stringify(voteData));
            const response = await fetch(VOTES_API_URL, { // POST /registration/register
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(voteData),
            });

            if (response.status === 201) { // Check for 201 Created status
                // const responseData = await response.json(); // Optional: if you need the returned vote object
                voteStatusP.textContent = 'Vote cast successfully!';
                voteStatusP.style.color = 'green';
                voterIdInput.value = '';
                candidateSelect.value = '';
                // Optionally refresh results automatically after voting
                fetchResults(); // Refresh results view
            } else {
                 // Try to get error message from response body (expected for 400 Bad Request)
                 let errorMsg = `Vote failed. Status: ${response.status}`;
                 try {
                    const errorText = await response.text(); // Get text body for error message
                    if(errorText) {
                        errorMsg = `Error: ${errorText}`;
                    }
                 } catch (e) { /* Ignore if body is empty or not text */ }
                throw new Error(errorMsg);
            }

        } catch (error) {
            console.error('Error casting vote:', error);
            voteStatusP.textContent = error.message; // Display specific error from backend or fetch
            voteStatusP.style.color = 'red';
        } finally {
            castVoteButton.disabled = false;
        }
    }

    // --- Event Listeners ---
    refreshCandidatesButton.addEventListener('click', async () => {
        await fetchCandidates();
        // After refreshing candidates, results might need refreshing too if new candidates added/removed
        await fetchResults();
    });
    refreshResultsButton.addEventListener('click', fetchResults);
    castVoteButton.addEventListener('click', castVote);
     updateVotesButton.addEventListener('click', updateVoteCounts);

    // --- Initial Load ---
    // Load candidates first, then results depend on candidateMap
    async function initialLoad() {
        await fetchCandidates();
        await fetchResults();
    }
    initialLoad(); // Run the initial load sequence

});