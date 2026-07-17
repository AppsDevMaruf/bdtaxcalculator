package com.maruf.bdtaxcalculator.ui.content

import com.maruf.bdtaxcalculator.tax.TaxDefaults
import com.maruf.bdtaxcalculator.tax.formatEnglishNumber
import kotlin.math.roundToLong

data class TaxFaq(
    val id: String,
    val banglaQuestion: String,
    val englishQuestion: String,
    val banglaAnswer: String,
    val englishAnswer: String
)

const val NbrFaqSourceUrl = "https://nbr.gov.bd/all-faq/eng"

fun incomeTaxFaqs(): List<TaxFaq> {
    val generalLimit = TaxDefaults.taxpayerTypes.first { it.id == "general" }.taxFreeLimit
    val womenLimit = TaxDefaults.taxpayerTypes.first { it.id == "women" }.taxFreeLimit
    val thirdGenderLimit = TaxDefaults.taxpayerTypes.first { it.id == "thirdGender" }.taxFreeLimit
    val disabledLimit = TaxDefaults.taxpayerTypes.first { it.id == "disabled" }.taxFreeLimit
    val freedomFighterLimit = TaxDefaults.taxpayerTypes.first { it.id == "freedomFighter" }.taxFreeLimit
    val minimumTax = TaxDefaults.minimumTax.roundToLong()
    val newAssessmentMinimumTax = TaxDefaults.newAssessmentMinimumTax.roundToLong()

    return listOf(
        TaxFaq(
            id = "what_is_income_tax",
            banglaQuestion = "আয়কর কি?",
            englishQuestion = "What is income tax?",
            banglaAnswer = "বাংলাদেশে আয়কর হলো ব্যক্তির বা প্রতিষ্ঠানের আয়, মুনাফা বা নির্দিষ্ট করযোগ্য প্রাপ্তির ওপর আইন অনুযায়ী ধার্য কর।",
            englishAnswer = "Income tax is a tax charged under Bangladesh income tax law on taxable income, profit, or specified receipts."
        ),
        TaxFaq(
            id = "how_to_pay_income_tax",
            banglaQuestion = "আয়কর কীভাবে পরিশোধ করতে হবে?",
            englishQuestion = "How do I pay income tax?",
            banglaAnswer = "সাধারণত a-Challan বা অনুমোদিত অনলাইন/ব্যাংকিং চ্যানেলের মাধ্যমে কর পরিশোধ করা যায়। উৎসে কর কাটা থাকলে সেটিও রিটার্নে দেখাতে হয়।",
            englishAnswer = "Tax can usually be paid through a-Challan or approved online/banking channels. Any deducted tax at source should be shown in the return."
        ),
        TaxFaq(
            id = "who_should_pay_income_tax",
            banglaQuestion = "কোন ব্যক্তি আয়কর প্রদানের জন্য উপযুক্ত?",
            englishQuestion = "Who is liable to pay income tax?",
            banglaAnswer = "যার করযোগ্য আয় প্রযোজ্য করমুক্ত সীমা অতিক্রম করে, তিনি আয়কর প্রদানের আওতায় আসেন। সাধারণ করদাতার বর্তমান app rule অনুযায়ী সীমা ${generalLimit.bnMoney()}।",
            englishAnswer = "A person becomes liable when taxable income exceeds the applicable tax-free threshold. In this app's current rule, the general threshold is ${generalLimit.enMoney()}."
        ),
        TaxFaq(
            id = "minimum_tax",
            banglaQuestion = "ন্যূনতম আয়কর কত?",
            englishQuestion = "What is the minimum income tax?",
            banglaAnswer = "অর্থ আইন ২০২৬ অনুযায়ী করমুক্ত সীমা অতিক্রম করলে সকল এলাকার করদাতার ন্যূনতম আয়কর ${minimumTax.bnMoney()}। নতুন করদাতার ক্ষেত্রে ন্যূনতম কর ${newAssessmentMinimumTax.bnMoney()}।",
            englishAnswer = "Under the Finance Act 2026, the minimum income tax after crossing the tax-free threshold is ${minimumTax.enMoney()} for taxpayers in every location. For a new taxpayer, it is ${newAssessmentMinimumTax.enMoney()}."
        ),
        TaxFaq(
            id = "tax_registration",
            banglaQuestion = "আয়কর নিবন্ধন কি?",
            englishQuestion = "What is income tax registration?",
            banglaAnswer = "আয়কর নিবন্ধন হলো করদাতাকে e-TIN বা TIN এর মাধ্যমে কর ব্যবস্থায় পরিচিত করার প্রক্রিয়া।",
            englishAnswer = "Income tax registration identifies a taxpayer in the tax system through an e-TIN or TIN."
        ),
        TaxFaq(
            id = "what_is_return",
            banglaQuestion = "রিটার্ন কি?",
            englishQuestion = "What is a tax return?",
            banglaAnswer = "রিটার্ন হলো নির্দিষ্ট করবর্ষের আয়, কর, রেয়াত, সম্পদ ও দায়ের ঘোষণাপত্র।",
            englishAnswer = "A return is a declaration of income, tax, rebate, assets, and liabilities for a tax year."
        ),
        TaxFaq(
            id = "who_files_return",
            banglaQuestion = "রিটার্ন কারা দাখিল করেন?",
            englishQuestion = "Who files a return?",
            banglaAnswer = "যাদের রিটার্ন দাখিল বাধ্যতামূলক বা যাদের করযোগ্য আয় আছে, তারা রিটার্ন দাখিল করেন। অনেক TINধারীর ক্ষেত্রেও রিটার্ন প্রয়োজন হতে পারে।",
            englishAnswer = "People for whom filing is mandatory, or who have taxable income, file returns. Many TIN holders may also need to file."
        ),
        TaxFaq(
            id = "where_to_file_return",
            banglaQuestion = "রিটার্ন কোথায় দাখিল করতে হয়?",
            englishQuestion = "Where should I file a return?",
            banglaAnswer = "রিটার্ন e-Return সিস্টেমে অনলাইনে বা প্রযোজ্য কর সার্কেলে দাখিল করা যায়। বাধ্যতামূলক e-Return হলে অনলাইন দাখিল করতে হবে।",
            englishAnswer = "A return may be filed online through e-Return or at the applicable tax circle. If e-Return is mandatory, it should be filed online."
        ),
        TaxFaq(
            id = "what_is_etin",
            banglaQuestion = "ই-টিআইএন কি?",
            englishQuestion = "What is e-TIN?",
            banglaAnswer = "e-TIN হলো অনলাইনে ইস্যুকৃত taxpayer identification number, যা করদাতার পরিচয় হিসেবে ব্যবহৃত হয়।",
            englishAnswer = "e-TIN is an online-issued taxpayer identification number used to identify a taxpayer."
        ),
        TaxFaq(
            id = "apply_etin",
            banglaQuestion = "আমি ই-টিআইএন এর জন্য কীভাবে আবেদন করতে পারি?",
            englishQuestion = "How can I apply for e-TIN?",
            banglaAnswer = "NBR-এর official e-TIN portal-এ গিয়ে NID/প্রযোজ্য পরিচয় তথ্য দিয়ে নিবন্ধন করা যায়।",
            englishAnswer = "You can register through the official NBR e-TIN portal using NID or other applicable identity information."
        ),
        TaxFaq(
            id = "etin_documents",
            banglaQuestion = "ই-টিআইএন প্রাপ্তির জন্য প্রয়োজনীয় ডকুমেন্ট কি লাগে?",
            englishQuestion = "What documents are needed for e-TIN?",
            banglaAnswer = "সাধারণত NID, মোবাইল নম্বর এবং করদাতার মৌলিক তথ্য লাগে। প্রতিষ্ঠান হলে নিবন্ধন/ট্রেড লাইসেন্স জাতীয় তথ্য প্রয়োজন হতে পারে।",
            englishAnswer = "Usually NID, mobile number, and basic taxpayer information are needed. Entities may need registration or trade licence-related details."
        ),
        TaxFaq(
            id = "tax_jurisdiction",
            banglaQuestion = "আমি আমার আয়কর অধিক্ষেত্র কোনটি তা কিভাবে বুঝব?",
            englishQuestion = "How do I know my tax jurisdiction?",
            banglaAnswer = "e-TIN সার্টিফিকেট, কর সার্কেলের তথ্য, ঠিকানা ও পেশা/ব্যবসার ধরন দেখে অধিক্ষেত্র জানা যায়। প্রয়োজন হলে সার্কেল অফিসে যোগাযোগ করুন।",
            englishAnswer = "Your e-TIN certificate, tax circle information, address, and profession/business type help identify jurisdiction. Contact the circle office if needed."
        ),
        TaxFaq(
            id = "return_filing_method",
            banglaQuestion = "রিটার্ন দাখিলের পদ্ধতি কি?",
            englishQuestion = "What is the return filing method?",
            banglaAnswer = "প্রযোজ্য ফরম পূরণ করে অনলাইনে e-Return দিয়ে বা কর সার্কেলে জমা দিয়ে রিটার্ন দাখিল করা যায়। হিসাব ও প্রমাণপত্র নিজের কাছে সংরক্ষণ করুন।",
            englishAnswer = "You can file by completing the applicable form online through e-Return or by submitting it to the tax circle. Keep calculations and supporting documents."
        ),
        TaxFaq(
            id = "return_deadline",
            banglaQuestion = "কোন সময়ের মধ্যে আয়কর রিটার্ন জমা দিতে হবে?",
            englishQuestion = "When should an income tax return be filed?",
            banglaAnswer = "ব্যক্তি করদাতার জন্য নির্ধারিত tax day বা NBR ঘোষিত সময়সীমার মধ্যে রিটার্ন জমা দিতে হয়।",
            englishAnswer = "Individual taxpayers should file within the tax day or deadline announced by NBR."
        ),
        TaxFaq(
            id = "change_address",
            banglaQuestion = "আমি ঠিকানা কিভাবে পরিবর্তন করতে পারি?",
            englishQuestion = "How can I change my address?",
            banglaAnswer = "e-TIN/e-Return profile বা সংশ্লিষ্ট কর সার্কেলের মাধ্যমে ঠিকানা সংশোধনের আবেদন করা যায়। প্রয়োজনীয় প্রমাণপত্র প্রস্তুত রাখুন।",
            englishAnswer = "You can request address correction through your e-TIN/e-Return profile or the related tax circle. Keep supporting documents ready."
        ),
        TaxFaq(
            id = "cancel_registration",
            banglaQuestion = "আমার আয় করমুক্ত সীমার নিচে নেমে গেলে নিবন্ধন বাতিল করা যাবে?",
            englishQuestion = "Can I cancel registration if income falls below the tax-free limit?",
            banglaAnswer = "আয় কমে গেলে রিটার্নে তা দেখাতে হতে পারে; TIN বাতিলের বিষয়টি সাধারণত কর কর্তৃপক্ষের নিয়ম ও অনুমোদনের ওপর নির্ভর করে।",
            englishAnswer = "If income falls, you may need to report it in a return. TIN cancellation generally depends on tax authority rules and approval."
        ),
        TaxFaq(
            id = "late_return",
            banglaQuestion = "নির্ধারিত সময়ে রিটার্ন না দিলে কি হবে?",
            englishQuestion = "What happens if I do not file on time?",
            banglaAnswer = "সময়মতো রিটার্ন না দিলে জরিমানা, বিলম্বজনিত কর বা অন্যান্য আইনি ফলাফল হতে পারে।",
            englishAnswer = "Late filing may result in penalty, delay-related tax, or other legal consequences."
        ),
        TaxFaq(
            id = "time_extension",
            banglaQuestion = "রিটার্ন দাখিলের জন্য কিভাবে সময় প্রার্থনা করতে হবে?",
            englishQuestion = "How can I request more time to file?",
            banglaAnswer = "নির্ধারিত সময়ের আগে সংশ্লিষ্ট কর কর্তৃপক্ষের কাছে নিয়ম অনুযায়ী সময় বৃদ্ধির আবেদন করতে হয়।",
            englishAnswer = "You should apply for extension to the relevant tax authority under the rules before the deadline."
        ),
        TaxFaq(
            id = "penalty_no_return",
            banglaQuestion = "আয়কর রিটার্ন দাখিল না করার শাস্তি কি?",
            englishQuestion = "What is the penalty for not filing a return?",
            banglaAnswer = "রিটার্ন না দিলে আয়কর আইনের অধীনে জরিমানা, অতিরিক্ত কর বা enforcement action হতে পারে।",
            englishAnswer = "Non-filing may lead to penalty, additional tax, or enforcement action under income tax law."
        ),
        TaxFaq(
            id = "return_form",
            banglaQuestion = "রিটার্ন ফরম কোথায় পাওয়া যায়?",
            englishQuestion = "Where can I get the return form?",
            banglaAnswer = "NBR website, e-Return portal বা কর অফিস থেকে রিটার্ন ফরম পাওয়া যায়।",
            englishAnswer = "Return forms are available on the NBR website, e-Return portal, or tax offices."
        ),
        TaxFaq(
            id = "mandatory_return",
            banglaQuestion = "আয়কর রিটার্ন কাদের জন্য বাধ্যতামূলক?",
            englishQuestion = "For whom is income tax return filing mandatory?",
            banglaAnswer = "করযোগ্য আয় আছে এমন ব্যক্তি, নির্দিষ্ট পেশা/সেবা/লাইসেন্সধারী, এবং NBR নিয়মে উল্লেখিত অন্যান্য ব্যক্তির জন্য রিটার্ন বাধ্যতামূলক হতে পারে।",
            englishAnswer = "Filing may be mandatory for people with taxable income, specified professions/services/licences, and other cases listed by NBR rules."
        ),
        TaxFaq(
            id = "what_is_income",
            banglaQuestion = "আয় কী?",
            englishQuestion = "What is income?",
            banglaAnswer = "আয় বলতে বেতন, ব্যবসা, পেশা, সম্পত্তি, বিনিয়োগ, মূলধনি লাভ বা অন্যান্য করযোগ্য উৎস থেকে প্রাপ্ত অর্থ/সুবিধা বোঝায়।",
            englishAnswer = "Income includes money or benefits from salary, business, profession, property, investment, capital gain, or other taxable sources."
        ),
        TaxFaq(
            id = "heads_of_income",
            banglaQuestion = "আয়করের জন্য আয়ের খাত কি কি?",
            englishQuestion = "What are the heads of income for tax?",
            banglaAnswer = "সাধারণত চাকরি, ভাড়া/সম্পত্তি, কৃষি, ব্যবসা/পেশা, মূলধনি আয় এবং অন্যান্য উৎস আলাদা খাত হিসেবে বিবেচিত হয়।",
            englishAnswer = "Common heads include salary, rent/property, agriculture, business/profession, capital gain, and income from other sources."
        ),
        TaxFaq(
            id = "total_income",
            banglaQuestion = "মোট আয় কী?",
            englishQuestion = "What is total income?",
            banglaAnswer = "সব করযোগ্য আয়ের যোগফল থেকে অনুমোদিত ছাড়/ব্যয় বাদ দিলে যে আয় করের জন্য বিবেচিত হয়, সেটিই মোট/করযোগ্য আয়।",
            englishAnswer = "Total or taxable income is generally the sum of taxable income after deducting allowed exemptions or expenses."
        ),
        TaxFaq(
            id = "firm_aop_income",
            banglaQuestion = "ফার্ম বা ব্যক্তিসংঘ হতে প্রাপ্ত আয় কি মোট আয়ের অন্তর্ভুক্ত হবে?",
            englishQuestion = "Is income from a firm or association of persons included in total income?",
            banglaAnswer = "হ্যাঁ, আইন অনুযায়ী করযোগ্য হলে ফার্ম বা ব্যক্তিসংঘ থেকে পাওয়া আয় মোট আয়ের অংশ হিসেবে দেখাতে হয়।",
            englishAnswer = "Yes, if taxable under the law, income from a firm or association of persons should be shown in total income."
        ),
        TaxFaq(
            id = "spouse_minor_income",
            banglaQuestion = "স্বামী/স্ত্রী বা অপ্রাপ্তবয়স্ক সন্তানের আয় কি করদাতার মোট আয়ের অন্তর্ভুক্ত হবে?",
            englishQuestion = "Is spouse or minor child's income included in total income?",
            banglaAnswer = "নির্দিষ্ট পরিস্থিতিতে spouse বা minor child's income করদাতার আয় হিসেবে যোগ হতে পারে। নিয়ম প্রয়োগে সার্কেল/কর উপদেষ্টার সহায়তা নিন।",
            englishAnswer = "In specific cases, spouse or minor child's income may be clubbed with the taxpayer's income. Check with the circle or a tax adviser."
        ),
        TaxFaq(
            id = "tax_free_limit",
            banglaQuestion = "আয়কর আরোপযোগ্য সীমা কি?",
            englishQuestion = "What is the tax-free threshold?",
            banglaAnswer = "২০২৬-২৭ করবর্ষে সাধারণ করদাতা ${generalLimit.bnMoney()}, মহিলা/৬৫+ ${womenLimit.bnMoney()}, তৃতীয় লিঙ্গ ${thirdGenderLimit.bnMoney()}, প্রতিবন্ধী ${disabledLimit.bnMoney()}, এবং গেজেটভুক্ত যুদ্ধাহত মুক্তিযোদ্ধা/আহত জুলাই যোদ্ধা ${freedomFighterLimit.bnMoney()} পর্যন্ত করমুক্ত। প্রতিবন্ধী সন্তান/পোষ্য প্রতি পিতা-মাতা বা আইনানুগ অভিভাবকের করমুক্ত সীমা আরও ${TaxDefaults.disabledDependentAllowance.bnMoney()} বাড়বে; বাবা-মা উভয়েই করদাতা হলে একজন এই সুবিধা পাবেন।",
            englishAnswer = "For tax year 2026-27, the thresholds are ${generalLimit.enMoney()} for general taxpayers, ${womenLimit.enMoney()} for female/65+, ${thirdGenderLimit.enMoney()} for third-gender taxpayers, ${disabledLimit.enMoney()} for persons with disability, and ${freedomFighterLimit.enMoney()} for gazetted war-wounded freedom fighters/injured July fighters. A parent or legal guardian receives an additional ${TaxDefaults.disabledDependentAllowance.enMoney()} for each disabled child/dependent; only one parent may claim it when both are taxpayers."
        ),
        TaxFaq(
            id = "individual_tax_rate",
            banglaQuestion = "২০২৬-২৭ ও ২০২৭-২৮ করবর্ষের ব্যক্তি কর স্ল্যাব কী?",
            englishQuestion = "What are the individual tax slabs for tax years 2026-27 and 2027-28?",
            banglaAnswer = "অর্থ আইন ২০২৬ অনুযায়ী কর স্ল্যাব:\n\n• প্রযোজ্য করমুক্ত সীমা পর্যন্ত — ০%\n• পরবর্তী ৩,০০,০০০ টাকা — ১০%\n• পরবর্তী ৪,০০,০০০ টাকা — ১৫%\n• পরবর্তী ৫,০০,০০০ টাকা — ২০%\n• পরবর্তী ২০,০০,০০০ টাকা — ২৫%\n• অবশিষ্ট আয়ের উপর — ৩০%\n\nকরমুক্ত সীমা করদাতার শ্রেণি অনুযায়ী নির্ধারিত হবে।",
            englishAnswer = "Tax slabs under the Finance Act 2026:\n\n• Up to the applicable tax-free threshold — 0%\n• Next BDT 300,000 — 10%\n• Next BDT 400,000 — 15%\n• Next BDT 500,000 — 20%\n• Next BDT 2,000,000 — 25%\n• Remaining income — 30%\n\nThe tax-free threshold depends on the taxpayer category."
        ),
        TaxFaq(
            id = "company_tax_rate",
            banglaQuestion = "কোম্পানি আয়করের জন্য কর হারের কাঠামো কি?",
            englishQuestion = "What is the company tax rate structure?",
            banglaAnswer = "কোম্পানির করহার কোম্পানির ধরন, listing status, sector এবং প্রযোজ্য আইনের ওপর নির্ভর করে। সর্বশেষ হার NBR নির্দেশনা থেকে যাচাই করা উচিত।",
            englishAnswer = "Company tax rates depend on company type, listing status, sector, and applicable law. Verify the latest rate from NBR guidance."
        ),
        TaxFaq(
            id = "tax_exempt_income",
            banglaQuestion = "করদাতার করমুক্ত আয়ের খাতসমূহ কি?",
            englishQuestion = "What income items are tax-exempt?",
            banglaAnswer = "আইনে উল্লেখিত নির্দিষ্ট ভাতা, সুবিধা, সঞ্চয়/প্রাপ্তি বা ছাড় করমুক্ত হতে পারে। প্রতিটি খাতের জন্য প্রযোজ্য সীমা আলাদা হতে পারে।",
            englishAnswer = "Specific allowances, benefits, savings/receipts, or exemptions listed in law may be tax-exempt. Each item may have its own limit."
        ),
        TaxFaq(
            id = "what_is_rebate",
            banglaQuestion = "কর রেয়াত কি?",
            englishQuestion = "What is tax rebate?",
            banglaAnswer = "কর রেয়াত হলো অনুমোদিত বিনিয়োগ/দান বা নির্দিষ্ট কারণে মোট কর থেকে বাদ দেওয়া সুবিধা।",
            englishAnswer = "Tax rebate is a deduction from calculated tax for eligible investment, donation, or other specified reasons."
        ),
        TaxFaq(
            id = "eligible_rebate_investment",
            banglaQuestion = "কোন ধরনের বিনিয়োগ/দান কর রেয়াত পাওয়ার উপযুক্ত?",
            englishQuestion = "Which investments or donations are eligible for rebate?",
            banglaAnswer = "NBR অনুমোদিত সঞ্চয়পত্র, DPS, life insurance, eligible securities/funds বা নির্দিষ্ট দান রেয়াতের আওতায় আসতে পারে।",
            englishAnswer = "NBR-approved savings certificates, DPS, life insurance, eligible securities/funds, or specified donations may qualify for rebate."
        ),
        TaxFaq(
            id = "tax_assessment",
            banglaQuestion = "কর নির্ধারণ কি?",
            englishQuestion = "What is tax assessment?",
            banglaAnswer = "কর নির্ধারণ হলো রিটার্ন ও প্রমাণ যাচাই করে করদাতার করদায় নির্ধারণের প্রক্রিয়া।",
            englishAnswer = "Tax assessment is the process of determining tax liability after reviewing the return and supporting information."
        ),
        TaxFaq(
            id = "tax_year",
            banglaQuestion = "করবর্ষ কি?",
            englishQuestion = "What is a tax year?",
            banglaAnswer = "করবর্ষ হলো রিটার্ন ও কর নির্ধারণের জন্য ব্যবহৃত assessment year। এই app-এ বর্তমান অ্যাসেসমেন্ট ${TaxDefaults.assessmentYearLabel}।",
            englishAnswer = "Tax year is the assessment year used for return and assessment. In this app, the current assessment year is 2026-27."
        ),
        TaxFaq(
            id = "tin_after_death",
            banglaQuestion = "বাবার মৃত্যুর পর তার টিআইএন বাতিল করার প্রয়োজন আছে কি?",
            englishQuestion = "Should a TIN be cancelled after the taxpayer dies?",
            banglaAnswer = "মৃত করদাতার ক্ষেত্রে উত্তরাধিকারী/প্রতিনিধি সংশ্লিষ্ট কর সার্কেলে তথ্য জানিয়ে প্রয়োজনীয় নির্দেশনা নেবেন।",
            englishAnswer = "For a deceased taxpayer, heirs or representatives should inform the relevant tax circle and follow their instructions."
        ),
        TaxFaq(
            id = "start_online_return",
            banglaQuestion = "আমি অনলাইনে রিটার্ন দাখিল করতে চাই। কিভাবে শুরু করব?",
            englishQuestion = "I want to file online. How do I start?",
            banglaAnswer = "e-Return portal-এ গিয়ে registration/sign-in করে রিটার্ন পূরণ শুরু করুন। TIN, NID, মোবাইল এবং আয়/কর তথ্য প্রস্তুত রাখুন।",
            englishAnswer = "Go to the e-Return portal, register or sign in, and start filling the return. Keep TIN, NID, mobile, income, and tax information ready."
        ),
        TaxFaq(
            id = "ereturn_sign_in",
            banglaQuestion = "e-Return সিস্টেমে সাইন-ইন করব কিভাবে?",
            englishQuestion = "How do I sign in to e-Return?",
            banglaAnswer = "e-Return portal-এ TIN/credentials দিয়ে সাইন-ইন করুন। OTP বা password verification প্রয়োজন হতে পারে।",
            englishAnswer = "Sign in to the e-Return portal with TIN/credentials. OTP or password verification may be required."
        ),
        TaxFaq(
            id = "ereturn_registration_requirements",
            banglaQuestion = "e-Return সিস্টেমে রেজিস্ট্রেশনের জন্য কি প্রয়োজন?",
            englishQuestion = "What is needed for e-Return registration?",
            banglaAnswer = "TIN, NID/জন্মতারিখের তথ্য এবং নিজের নামে নিবন্ধিত মোবাইল নম্বর সাধারণত প্রয়োজন হয়।",
            englishAnswer = "TIN, NID/date of birth details, and a mobile number registered in your name are usually required."
        ),
        TaxFaq(
            id = "ereturn_registration_process",
            banglaQuestion = "e-Return সিস্টেমে রেজিস্ট্রেশনের পদ্ধতি কি?",
            englishQuestion = "What is the e-Return registration process?",
            banglaAnswer = "TIN ও মোবাইল যাচাই করে OTP/password সেট করলে account তৈরি হয়। এরপর profile ও return তথ্য পূরণ করা যায়।",
            englishAnswer = "After verifying TIN and mobile, set OTP/password to create an account. Then complete profile and return details."
        ),
        TaxFaq(
            id = "old_mobile_unavailable",
            banglaQuestion = "টিআইএন খোলার সময় ব্যবহৃত মোবাইল নম্বর না থাকলে কি e-Return করা যাবে?",
            englishQuestion = "Can I use e-Return if the old TIN mobile number is unavailable?",
            banglaAnswer = "মোবাইল নম্বর পরিবর্তন বা verification সমস্যার জন্য e-Return helpdesk/কর সার্কেলের সহায়তা নিতে হতে পারে।",
            englishAnswer = "For mobile number change or verification issues, you may need help from the e-Return helpdesk or tax circle."
        ),
        TaxFaq(
            id = "password_reset",
            banglaQuestion = "পাসওয়ার্ড কিভাবে সেট বা রিসেট করব?",
            englishQuestion = "How do I set or reset the password?",
            banglaAnswer = "e-Return portal-এর password setup/forgot password option ব্যবহার করে OTP verification দিয়ে পাসওয়ার্ড সেট বা রিসেট করা যায়।",
            englishAnswer = "Use the password setup or forgot password option in the e-Return portal and verify by OTP."
        ),
        TaxFaq(
            id = "mobile_ownership_check",
            banglaQuestion = "মোবাইল নম্বর আমার নামে নিবন্ধিত কিনা তা কিভাবে জানতে পারব?",
            englishQuestion = "How do I know whether the mobile number is registered in my name?",
            banglaAnswer = "মোবাইল অপারেটরের official method বা customer care থেকে SIM ownership যাচাই করা যায়।",
            englishAnswer = "You can verify SIM ownership through your mobile operator's official method or customer care."
        ),
        TaxFaq(
            id = "ereturn_help",
            banglaQuestion = "e-Return সমস্যার ক্ষেত্রে সহায়তার জন্য কোথায় যোগাযোগ করব?",
            englishQuestion = "Where can I get help for e-Return issues?",
            banglaAnswer = "e-Return helpdesk, NBR hotline/website, অথবা সংশ্লিষ্ট কর সার্কেলে যোগাযোগ করুন।",
            englishAnswer = "Contact the e-Return helpdesk, NBR hotline/website, or your relevant tax circle."
        ),
        TaxFaq(
            id = "mobile_return",
            banglaQuestion = "মোবাইল ফোনে কি e-Return তৈরি করা যাবে?",
            englishQuestion = "Can I prepare e-Return on a mobile phone?",
            banglaAnswer = "হ্যাঁ, supported mobile browser দিয়ে e-Return portal ব্যবহার করা যায়; বড় screen হলে তথ্য পূরণ সহজ হয়।",
            englishAnswer = "Yes, you can use the e-Return portal in a supported mobile browser, though a larger screen may be easier."
        ),
        TaxFaq(
            id = "supporting_documents_online",
            banglaQuestion = "অনলাইনে রিটার্ন দাখিলের পর সাপোর্টিং কাগজপত্র কোথায় জমা দেব?",
            englishQuestion = "Where do I submit supporting documents after online filing?",
            banglaAnswer = "অনলাইনে দাখিলের পর সাধারণত কাগজপত্র নিজের কাছে সংরক্ষণ করতে হয়; NBR/সার্কেল চাইলে পরে জমা দিতে হবে।",
            englishAnswer = "After online filing, usually keep supporting documents with you and submit only if NBR or the circle asks later."
        ),
        TaxFaq(
            id = "submit_again_after_online",
            banglaQuestion = "অনলাইনে রিটার্ন দাখিলের পর সার্কেলে আবার জমা দিতে হবে কি?",
            englishQuestion = "Do I need to submit again at the tax circle after online filing?",
            banglaAnswer = "সাধারণত সফল e-Return submission হলে একই রিটার্ন আবার সার্কেলে জমা দিতে হয় না।",
            englishAnswer = "Usually, after successful e-Return submission, the same return does not need to be submitted again at the circle."
        ),
        TaxFaq(
            id = "start_filling_return",
            banglaQuestion = "রেজিস্ট্রেশনের পর কিভাবে রিটার্ন পূরণ শুরু করব?",
            englishQuestion = "After registration, how do I start filling the return?",
            banglaAnswer = "Sign-in করে return filing menu থেকে taxpayer info, income, tax payment, assets/liabilities ধাপে ধাপে পূরণ করুন।",
            englishAnswer = "Sign in and use the return filing menu to fill taxpayer info, income, tax payment, assets, and liabilities step by step."
        ),
        TaxFaq(
            id = "online_submission_process",
            banglaQuestion = "অনলাইনে রিটার্ন জমা দেবার প্রক্রিয়া কি?",
            englishQuestion = "What is the online return submission process?",
            banglaAnswer = "তথ্য পূরণ, tax/payment verification, preview, declaration এবং final submit সম্পন্ন করলে acknowledgement পাওয়া যায়।",
            englishAnswer = "Complete information entry, tax/payment verification, preview, declaration, and final submission to get acknowledgement."
        ),
        TaxFaq(
            id = "tds_advance_tax",
            banglaQuestion = "উৎসে কর ও অগ্রিম কর থাকলে কি অনলাইনে রিটার্ন দাখিল করা যাবে?",
            englishQuestion = "Can I file online if tax was deducted at source or paid in advance?",
            banglaAnswer = "হ্যাঁ, উৎসে কর/অগ্রিম করের তথ্য রিটার্নে উল্লেখ করে প্রযোজ্য credit claim করা যায়। এই app-এর ‘পরিশোধিত কর সমন্বয়’ অংশে কেবল চালান, withholding certificate বা যাচাইকৃত রেকর্ড অনুযায়ী সমন্বয়যোগ্য অর্থ দিন।",
            englishAnswer = "Yes, applicable TDS or advance tax credit may be claimed in the return. In this app's Paid Tax Adjustment section, enter only adjustable amounts supported by a challan, withholding certificate, or verified record."
        ),
        TaxFaq(
            id = "claim_tax_credit",
            banglaQuestion = "কর্তিত উৎসে কর ও অগ্রিম করের ক্রেডিট কিভাবে পাব?",
            englishQuestion = "How do I get credit for deducted or advance tax?",
            banglaAnswer = "চালান/withholding certificate/auto-matched তথ্য ব্যবহার করে tax payment section-এ credit claim করুন।",
            englishAnswer = "Use challan, withholding certificate, or auto-matched records and claim credit in the tax payment section."
        ),
        TaxFaq(
            id = "salary_tds_credit",
            banglaQuestion = "বেতন হতে উৎসে করের ক্রেডিট কিভাবে পাব?",
            englishQuestion = "How do I claim salary TDS credit?",
            banglaAnswer = "নিয়োগকর্তার tax deduction certificate বা payroll তথ্য অনুযায়ী salary TDS দেখাতে হয়।",
            englishAnswer = "Show salary TDS using employer tax deduction certificate or payroll information."
        ),
        TaxFaq(
            id = "bank_sanchaypatra_dividend_tds",
            banglaQuestion = "ব্যাংক, সঞ্চয়পত্র ও ডিভিডেন্ডের উৎসে করের ক্রেডিট কিভাবে দাবী করব?",
            englishQuestion = "How do I claim TDS credit from bank, savings certificates, or dividend?",
            banglaAnswer = "ব্যাংক statement/certificate, সঞ্চয়পত্র registration এবং dividend certificate অনুযায়ী উৎসে কর দেখান।",
            englishAnswer = "Use bank statements/certificates, savings certificate registration, and dividend certificates to show TDS."
        ),
        TaxFaq(
            id = "honorarium_income",
            banglaQuestion = "মিটিং ফি, ট্রেনিং ফি বা সম্মানী কোন আয়ে দেখাব?",
            englishQuestion = "Where do I show meeting fee, training fee, or honorarium?",
            banglaAnswer = "প্রকৃতি অনুযায়ী এগুলো পেশাগত আয় বা অন্যান্য উৎসের আয় হিসেবে দেখানো হতে পারে। নিশ্চিত না হলে সার্কেলের পরামর্শ নিন।",
            englishAnswer = "Depending on the nature, these may be shown as professional income or income from other sources. Ask the circle if unsure."
        ),
        TaxFaq(
            id = "old_sanchaypatra_tds",
            banglaQuestion = "পুরনো সঞ্চয়পত্রের উৎসে করের ক্রেডিট কিভাবে পাব?",
            englishQuestion = "How do I claim TDS credit for old savings certificates?",
            banglaAnswer = "পুরনো certificate/statement এবং কর কর্তনের প্রমাণ থাকলে tax payment section-এ তথ্য দিন; mismatch হলে support নিন।",
            englishAnswer = "Use old certificates/statements and proof of deduction in the tax payment section; get support if there is a mismatch."
        ),
        TaxFaq(
            id = "joint_sanchaypatra",
            banglaQuestion = "যৌথ সঞ্চয়পত্রের মুনাফা ও উৎসে করের তথ্য কিভাবে দেখাব?",
            englishQuestion = "How do I show income and TDS from joint savings certificates?",
            banglaAnswer = "যৌথ মালিকানার অংশ, certificate তথ্য ও actual benefit অনুযায়ী income/TDS দেখাতে হয়।",
            englishAnswer = "Show income and TDS based on ownership share, certificate details, and actual benefit."
        ),
        TaxFaq(
            id = "car_advance_tax",
            banglaQuestion = "গাড়ির অগ্রিম করের ক্রেডিট কিভাবে দাবী করব?",
            englishQuestion = "How do I claim advance tax paid for a car?",
            banglaAnswer = "গাড়ির registration/BRTA payment তথ্য অনুযায়ী advance tax credit claim করা যায়।",
            englishAnswer = "Claim advance tax credit using vehicle registration or BRTA payment information."
        ),
        TaxFaq(
            id = "pay_tax_ereturn",
            banglaQuestion = "e-Return সিস্টেমের মাধ্যমে কর পরিশোধ করা যায় কি?",
            englishQuestion = "Can tax be paid through e-Return?",
            banglaAnswer = "e-Return থেকে supported payment channel বা a-Challan ব্যবহার করে কর পরিশোধ করা যায়।",
            englishAnswer = "Tax can be paid using supported payment channels or a-Challan through the e-Return flow."
        ),
        TaxFaq(
            id = "excess_tax_previous_year",
            banglaQuestion = "পূর্ববর্তী বছরের অতিরিক্ত পরিশোধিত কর কিভাবে দেখাব?",
            englishQuestion = "How do I show excess tax paid in a previous year?",
            banglaAnswer = "পূর্ববর্তী assessment-এর excess payment/refund adjustment তথ্য tax payment/adjustment অংশে দেখাতে হয়।",
            englishAnswer = "Show previous assessment excess payment or refund adjustment in the tax payment/adjustment section."
        ),
        TaxFaq(
            id = "wrong_gender",
            banglaQuestion = "আমি মহিলা, কিন্তু পুরুষ হিসেবে কর হিসাব হচ্ছে। কারণ কি?",
            englishQuestion = "I am female, but tax is calculated as male. Why?",
            banglaAnswer = "NID/e-TIN profile-এ gender data mismatch থাকলে এমন হতে পারে। profile সংশোধন বা helpdesk/সার্কেলের সহায়তা নিন।",
            englishAnswer = "This may happen if gender data mismatches in NID/e-TIN profile. Correct the profile or contact helpdesk/circle."
        ),
        TaxFaq(
            id = "ereturn_benefits",
            banglaQuestion = "e-Return এ আয়কর রিটার্ন জমা প্রদানের সুবিধা কি?",
            englishQuestion = "What are the benefits of filing through e-Return?",
            banglaAnswer = "ঘরে বসে রিটার্ন দাখিল, acknowledgement download, tax calculation guidance, এবং কম কাগজপত্রে প্রক্রিয়া সম্পন্ন করার সুবিধা পাওয়া যায়।",
            englishAnswer = "You can file from home, download acknowledgement, get calculation guidance, and complete the process with less paperwork."
        ),
        TaxFaq(
            id = "mandatory_ereturn_2025_26",
            banglaQuestion = "২০২৫-২৬ করবর্ষে কার জন্য e-Return দাখিল বাধ্যতামূলক?",
            englishQuestion = "For whom is e-Return mandatory in tax year 2025-26?",
            banglaAnswer = "NBR যে ব্যক্তি/শ্রেণির জন্য বাধ্যতামূলক ঘোষণা করে, তাদের e-Return দাখিল করতে হবে। সর্বশেষ নির্দেশনা official NBR/e-Return notice থেকে দেখুন।",
            englishAnswer = "People or categories declared by NBR as mandatory must file through e-Return. Check the latest official NBR/e-Return notice."
        ),
        TaxFaq(
            id = "asset_previous_year",
            banglaQuestion = "গতবছর সম্পদ বিবরণীতে তথ্য দিয়েছি, এবারও কি পূরণ করতে হবে?",
            englishQuestion = "I entered asset details last year. Do I need to enter them again?",
            banglaAnswer = "আগের তথ্য auto-filled হলেও চলতি বছরের পরিবর্তন, সংযোজন বা হ্রাস যাচাই করে update করতে হবে।",
            englishAnswer = "Even if previous data is auto-filled, verify and update any addition, disposal, or change for the current year."
        ),
        TaxFaq(
            id = "printed_return_not_submitted",
            banglaQuestion = "গতবার online return তৈরি করে print copy জমা দিয়েছি; এবার কী করব?",
            englishQuestion = "Last year I prepared online and submitted a print copy. What should I do this year?",
            banglaAnswer = "এবার e-Return submission flow complete করে online acknowledgement নেওয়া ভালো, বিশেষ করে e-Return বাধ্যতামূলক হলে।",
            englishAnswer = "This year, complete the e-Return submission flow and obtain online acknowledgement, especially if e-Return is mandatory."
        ),
        TaxFaq(
            id = "bank_tds_mismatch",
            banglaQuestion = "Bank TDS mismatch বা বেশি claim দেখালে কী করব?",
            englishQuestion = "What should I do if Bank TDS is mismatched or claimed more than allowed?",
            banglaAnswer = "ব্যাংক certificate/statement মিলিয়ে সঠিক TDS amount দিন। system mismatch থাকলে bank/NBR support-এর সহায়তা নিন।",
            englishAnswer = "Match the TDS amount with bank certificate/statement. If the system record mismatches, seek help from bank or NBR support."
        ),
        TaxFaq(
            id = "tax_service_month",
            banglaQuestion = "কর সেবা মাসে কি e-Return সহায়তা পাওয়া যাবে?",
            englishQuestion = "Is e-Return help available during Tax Service Month?",
            banglaAnswer = "সাধারণত কর সেবা মাসে NBR helpdesk/সার্কেল অফিস থেকে রিটার্ন ও e-Return সহায়তা পাওয়া যায়।",
            englishAnswer = "Usually, return and e-Return support is available from NBR helpdesk or tax circle offices during Tax Service Month."
        ),
        TaxFaq(
            id = "car_tax_adjustment",
            banglaQuestion = "গাড়ির কর সমন্বয় হচ্ছে না। করণীয় কি?",
            englishQuestion = "Car tax is not being adjusted. What should I do?",
            banglaAnswer = "গাড়ির registration number, payment date, challan/BRTA record ঠিক আছে কি না যাচাই করুন; mismatch হলে helpdesk-এ জানান।",
            englishAnswer = "Check vehicle registration number, payment date, challan/BRTA record, and contact helpdesk if there is a mismatch."
        ),
        TaxFaq(
            id = "two_employers",
            banglaQuestion = "এক বছরে দুই প্রতিষ্ঠানে কাজ করলে আয় কিভাবে দেখাব?",
            englishQuestion = "How do I show income from two employers in one year?",
            banglaAnswer = "দুই প্রতিষ্ঠানের বেতন, allowance, bonus এবং TDS আলাদা source অনুযায়ী যোগ করে মোট salary income দেখাতে হবে।",
            englishAnswer = "Add salary, allowances, bonus, and TDS from both employers by source and show the total salary income."
        ),
        TaxFaq(
            id = "motorcycle_assets",
            banglaQuestion = "সম্পদ ৪ কোটির কম হলে মোটরসাইকেল কি দেখাতে হবে?",
            englishQuestion = "If assets are below BDT 4 crore, should I show a motorcycle?",
            banglaAnswer = "সম্পদ বিবরণী প্রযোজ্য হলে মোটরসাইকেলসহ নিজের সম্পদ সঠিকভাবে দেখানো উচিত। mandatory condition আলাদা হতে পারে।",
            englishAnswer = "If an asset statement applies, personal assets including a motorcycle should be shown correctly. Mandatory conditions may differ."
        ),
        TaxFaq(
            id = "sanchaypatra_data_not_found",
            banglaQuestion = "সঞ্চয়পত্রের তথ্য সার্চ করলে Data not found দেখায় কেন?",
            englishQuestion = "Why does savings certificate search show Data not found?",
            banglaAnswer = "registration number, NID/TIN mapping বা source data update না হলে এমন হতে পারে। certificate তথ্য যাচাই করে support নিন।",
            englishAnswer = "This may happen due to registration number, NID/TIN mapping, or source data update issues. Verify certificate details and contact support."
        ),
        TaxFaq(
            id = "asset_market_value",
            banglaQuestion = "স্বর্ণ ও অন্যান্য সম্পদের কি বাজার মূল্য দেখাব?",
            englishQuestion = "Should I show market value for gold and other assets?",
            banglaAnswer = "সম্পদ সাধারণত acquisition cost বা প্রযোজ্য disclosure rule অনুযায়ী দেখাতে হয়। valuation নিয়ে সন্দেহ হলে tax adviser/সার্কেলের সহায়তা নিন।",
            englishAnswer = "Assets are generally shown using acquisition cost or applicable disclosure rules. Ask a tax adviser/circle if valuation is unclear."
        ),
        TaxFaq(
            id = "nid_correction_not_updated",
            banglaQuestion = "NID সংশোধনের পর e-Return পুরনো তথ্য দেখালে কী করব?",
            englishQuestion = "What if e-Return still shows old information after NID correction?",
            banglaAnswer = "NID/e-TIN data sync হতে সময় লাগতে পারে। প্রয়োজন হলে e-TIN profile update বা helpdesk-এ request দিন।",
            englishAnswer = "NID/e-TIN data may take time to sync. Update the e-TIN profile or request helpdesk support if needed."
        ),
        TaxFaq(
            id = "doctor_lawyer_income",
            banglaQuestion = "চিকিৎসক/আইনজীবী হিসেবে e-Return এ কোথায় আয় দেখাব?",
            englishQuestion = "Where do doctors or lawyers show income in e-Return?",
            banglaAnswer = "চিকিৎসক/আইনজীবীর আয় সাধারণত profession/business income হিসেবে দেখানো হয়। উৎস ও হিসাব অনুযায়ী সঠিক খাত নির্বাচন করুন।",
            englishAnswer = "Doctors' or lawyers' income is generally shown as professional/business income. Choose the proper head based on source and records."
        ),
        TaxFaq(
            id = "all_assets_current_year",
            banglaQuestion = "বিগত বছরের সব সম্পদ কি এ বছর প্রদর্শন করতে হবে?",
            englishQuestion = "Do all previous assets need to be shown this year?",
            banglaAnswer = "সম্পদ বিবরণী প্রযোজ্য হলে opening asset, current additions/disposals এবং closing balance ধারাবাহিকভাবে দেখাতে হবে।",
            englishAnswer = "If an asset statement applies, show opening assets, current additions/disposals, and closing balance consistently."
        ),
        TaxFaq(
            id = "payment_deducted_not_updated",
            banglaQuestion = "e-Return এ টাকা কেটে নিয়েছে কিন্তু payment update হয়নি। কী করব?",
            englishQuestion = "Payment was deducted in e-Return but not updated. What should I do?",
            banglaAnswer = "payment/challan reference সংরক্ষণ করুন। কিছু সময় অপেক্ষা করুন; update না হলে payment channel বা e-Return support-এ যোগাযোগ করুন।",
            englishAnswer = "Keep the payment/challan reference. Wait for sync, and if it remains unresolved, contact the payment channel or e-Return support."
        ),
        TaxFaq(
            id = "amend_online_return",
            banglaQuestion = "অনলাইনে দাখিলকৃত রিটার্নে ভুল হলে কিভাবে সংশোধন করব?",
            englishQuestion = "How can I correct a mistake in an online return?",
            banglaAnswer = "সংশোধিত return/amendment facility বা কর সার্কেলের নির্দেশনা অনুযায়ী সংশোধন করতে হবে।",
            englishAnswer = "Use the amended return facility or follow the tax circle's instruction for correction."
        ),
        TaxFaq(
            id = "mpo_teacher_salary",
            banglaQuestion = "এমপিওভুক্ত শিক্ষক-কর্মচারীর বেতন সরকারি না বেসরকারি হিসেবে ধরা হবে?",
            englishQuestion = "Is MPO teacher/employee salary treated as government or private?",
            banglaAnswer = "MPO বেতনের classification নির্ভর করে প্রযোজ্য NBR নির্দেশনা ও employer তথ্যের ওপর। payroll/tax certificate দেখে নির্বাচন করুন।",
            englishAnswer = "Classification depends on applicable NBR guidance and employer information. Use payroll or tax certificate details."
        ),
        TaxFaq(
            id = "ereturn_data_safety",
            banglaQuestion = "অনলাইনে রিটার্ন দাখিল করলে আমার তথ্য কি নিরাপদ থাকবে?",
            englishQuestion = "Is my information safe if I file online?",
            banglaAnswer = "official e-Return portal ব্যবহার করলে তথ্য NBR system-এ যায়। password/OTP কারও সাথে share করবেন না এবং official URL ব্যবহার করুন।",
            englishAnswer = "When using the official e-Return portal, data goes to the NBR system. Do not share password/OTP and use the official URL."
        ),
        TaxFaq(
            id = "certified_copy",
            banglaQuestion = "অনলাইনে রিটার্ন দাখিল করলে সার্টিফাইড কপি পাওয়া যায়?",
            englishQuestion = "Can I get a certified copy after online filing?",
            banglaAnswer = "online acknowledgement/certificate download করা যায়। certified copy দরকার হলে NBR/e-Return নির্দেশনা অনুসরণ করুন।",
            englishAnswer = "Online acknowledgement or certificate can be downloaded. For certified copy needs, follow NBR/e-Return instructions."
        ),
        TaxFaq(
            id = "late_online_return",
            banglaQuestion = "সময় পার হলে কি অনলাইন রিটার্ন দাখিল করা যাবে?",
            englishQuestion = "Can an online return be filed after the deadline?",
            banglaAnswer = "সময় পার হলে online filing facility ও penalty rules NBR নির্দেশনার ওপর নির্ভর করে। দেরি হলে দ্রুত সার্কেল/official portal check করুন।",
            englishAnswer = "After the deadline, online filing availability and penalty rules depend on NBR guidance. Check the portal or circle quickly."
        )
    )
}

private fun Long.bnMoney(): String = "${formatEnglishNumber(this).toBanglaDigits()} টাকা"

private fun Long.enMoney(): String = "BDT ${formatEnglishNumber(this)}"

private fun String.toBanglaDigits(): String {
    val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    return map { char ->
        if (char.isDigit()) bengaliDigits[char.digitToInt()] else char
    }.joinToString("")
}
