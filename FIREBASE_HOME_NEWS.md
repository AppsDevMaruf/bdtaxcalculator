# Firebase Home News Ticker

The Home screen news ticker reads these Firebase Remote Config parameters:

| Parameter key | Type | Example value |
| --- | --- | --- |
| `home_news_enabled` | Boolean | `true` |
| `home_news_text_bn` | String | `৩০ সেপ্টেম্বরের মধ্যে রিটার্ন দিন—পরিশোধযোগ্য করের ৫% ছাড়, সর্বোচ্চ ৳২৫,০০০।` |
| `home_news_text_en` | String | `File by 30 September—get 5% off tax payable, up to BDT 25,000.` |
| `home_news_url` | String | `https://nbr.gov.bd/uploads/public-notice/Press_Release_Incentives.pdf` |
| `tax_notices_json` | JSON string | See the example below |

`tax_notices_json` controls the full notice-list screen opened from Quick
Services. Keep the newest notice first.

```json
[
  {
    "id": "early_filing_incentive_2026",
    "title_bn": "৩০ সেপ্টেম্বরের মধ্যে আয়কর রিটার্ন দাখিলে ৫% পর্যন্ত কর প্রণোদনা সংক্রান্ত প্রেস রিলিজ",
    "title_en": "Press release on tax incentive for filing returns by 30 September",
    "published_date": "02-08-2026",
    "url": "https://nbr.gov.bd/uploads/public-notice/Press_Release_Incentives.pdf",
    "is_active": true
  }
]
```

## Publish an update

1. Open Firebase Console for the app project.
2. Go to **Remote Config**.
3. Create or update the parameters above. Enter `tax_notices_json` as a JSON
   string value.
4. Review and publish the Remote Config changes.

The app accepts only `https://` links. It checks Remote Config on app launch and
uses Firebase's recommended 12-hour production minimum fetch interval. Reopening
the app inside that window uses the persistent local cache instead of making
another backend fetch. If Firebase is unavailable or a value is empty, the
built-in official NBR incentive notice remains visible. Set
`home_news_enabled` to `false` and publish to hide the ticker remotely.
