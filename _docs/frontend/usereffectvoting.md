---
title: "User Dashboard & Voting"
permalink: /docs/frontend/usereffectvoting/
excerpt: "Voter dashboard and secure voting process"
last_modified_at: 2026-01-21
toc: true
---

# User Dashboard & Voting

The User Dashboard is the central hub for voters. It allows users to view active elections, cast their votes securely using face verification, and view results of past elections.

## Dashboard Overview

The dashboard is divided into two main sections:

1.  **Active Elections**: Elections currently open for voting.
2.  **Past Election Results**: Completed elections where results have been published.

## Voting Process

The voting process is designed to be secure and verifiable.

1.  **Selection**: User clicks "Vote Now" on an active election card.
2.  **Candidate Review**: A modal appears displaying the list of candidates, their party symbols, photos, and manifestos.
3.  **Face Verification**: The system activates the user's webcam. A live feed is displayed to ensure the user is present.
4.  **Submission**: When the user clicks "Vote" for a specific candidate:
    *   The webcam captures a snapshot.
    *   The image is converted to a Blob.
    *   The vote and the image are sent to the backend for verification.

### Code Snippet: Voting Logic

```jsx
const handleVote = async (candidateId) => {
    if (window.confirm('Are you sure? This will capture your image for verification.')) {
        try {
            setIsVerifying(true);
            // 1. Capture Image
            const imageSrc = webcamRef.current.getScreenshot();
            
            // 2. Convert to Blob
            const blob = dataURItoBlob(imageSrc);

            // 3. Send Vote + Image
            await castVote(selectedElection.electionId, candidateId, blob);
            
            alert('Vote cast successfully!');
            loadElections(); // Refresh status
        } catch (e) {
            alert('Voting failed: ' + e.message);
        }
    }
};
```

## Viewing Results

For completed elections, users can click "View Results". This opens a modal displaying a ranked table of candidates, vote counts, and percentages.

## Verification Status

The dashboard tracks the user's voting status locally using a `hasVotedMap`. If a user has already voted in an election, the "Vote Now" button is replaced with a disabled "You Voted" button.