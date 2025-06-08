# Firebase Cloud Function

This Cloud Function sends a push notification to every device subscribed to the
`blood_requests` topic whenever a new document is added to the
`bloodRequests` collection in Firestore.

Deploy with:

```bash
firebase deploy --only functions
```

Make sure the Firebase project has Cloud Messaging enabled.
