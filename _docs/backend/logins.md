---
title: "Logins & Authentication"
permalink: /docs/backend/logins/
excerpt: "Authentication flow and login mechanisms"
last_modified_at: 2026-01-21
toc: true
---

# Authentication & Login

The Online Voting System uses a secure JWT (JSON Web Token) based authentication mechanism.

## Login Flow

1.  **User Submission**: Client sends `POST /api/auth/login` with `email` and `password`.
2.  **Verification**:
    *   System looks up user by email.
    *   Compares submitted password with stored `BCrypt` hash.
3.  **Token Generation**:
    *   If valid, server generates a JWT containing:
        *   `sub` (Subject/Email)
        *   `role` (USER/ADMIN)
        *   `iat` (Issued At)
        *   `exp` (Expiration)
4.  **Response**: Server returns the JWT to the client.

## Request/Response Example

**Request:**
```json
POST /api/auth/login
{
  "email": "user@example.com",
  "password": "securepassword"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2Vy... (truncated)",
  "type": "Bearer",
  "id": 1,
  "username": "user@example.com",
  "roles": ["ROLE_USER"]
}
```

## Security Measures

*   **BCrypt Hashing**: Passwords are never stored in plain text.
*   **Role-Based Access Control (RBAC)**: JWTs contain roles that guard specific endpoints.
*   **Stateless**: The server does not store session state; the token is sufficient for verification.

## Data Seeding Logins

| Voter ID | Full Name | Password | District & State |
|---|---|---|---|
| VOT9900000001 | Rajesh Kumar | RajeshKumar | Mumbai, Maharashtra |
| VOT2345678901 | Priya Sharma | PriyaSharma | Delhi, Delhi |
| VOT9900000003 | Amit Patel | AmitPatel | Bangalore, Karnataka |
| VOT4567890123 | Sneha Desai | SnehaDesai | Pune, Maharashtra |
| VOT9900000005 | Vikram Singh | VikramSingh | Jaipur, Rajasthan |
| VOT6789012345 | Anita Verma | AnitaVerma | Kolkata, West Bengal |
| VOT9900000007 | Rahul Mehta | RahulMehta | Ahmedabad, Gujarat |
| VOT7890123456 | Kavita Nair | KavitaNair | Chennai, Tamil Nadu |
| VOT9900000009 | Suresh Reddy | SureshReddy | Hyderabad, Telangana |
| VOT8901234567 | Meena Iyer | MeenaIyer | Kochi, Kerala |
| VOT9900000011 | Anil Gupta | AnilGupta | Lucknow, Uttar Pradesh |
| VOT9012345678 | Pooja Joshi | PoojaJoshi | Indore, Madhya Pradesh |
| VOT9900000013 | Deepak Saxena | DeepakSaxena | Bhopal, Madhya Pradesh |
| VOT0123456789 | Neha Kapoor | NehaKapoor | Shimla, Himachal Pradesh |
| VOT9900000015 | Ravi Krishnan | RaviKrishnan | Madurai, Tamil Nadu |
| VOT1234567891 | Shalini Das | ShaliniDas | Guwahati, Assam |
| VOT9900000017 | Manish Yadav | ManishYadav | Patna, Bihar |
| VOT2345678902 | Divya Menon | DivyaMenon | Thiruvananthapuram, Kerala |
| VOT9900000019 | Arjun Pandey | ArjunPandey | Nagpur, Maharashtra |
| VOT3456789013 | Ritika Singh | RitikaSingh | Dehradun, Uttarakhand |
| VOT1000000001 | Madhur Chaudhari | MadhurChaudhari | Jalgaon, Maharashtra |
| VOT1000000002 | Anuj Tomar | AnujTomar | Agra, Uttar Pradesh |
| VOT1000000003 | Satyam Patel | SatyamPatel | Katni, Madhya Pradesh |
| VOT1000000004 | Soham Kumar Dey | SohamKumarDey | Murshidabad, West Bengal |
| VOT1000000005 | Utkarsh Phalphale | UtkarshPhalphale | Pune, Maharashtra |
| VOT1000000006 | Rajat Morya | RajatMorya | Bareilly, Uttar Pradesh |
| VOT1000000007 | Ayush Kush | AyushKush | Lucknow, Uttar Pradesh |
| VOT1000000008 | Ninad Soman | NinadSoman | Pune, Maharashtra |
| VOT1000000009 | Vivek Yadav | VivekYadav | Nalanda, Bihar |
| VOT1000000010 | Satyam Bavankar | SatyamBavankar | Seoni, Madhya Pradesh |
| VOT1000000011 | Tanay Mapare | TanayMapare | Washim, Maharashtra |
| VOT1000000012 | Ayush Shrivastava | AyushShrivastava | Lucknow, Uttar Pradesh |
| VOT1000000013 | Ansh Mittal | AnshMittal | Gwalior, Madhya Pradesh |
| VOT1000000014 | Rajdeep Kala | RajdeepKala | Ujjain, Madhya Pradesh |
| VOT1000000015 | Satyendra Rathore | SatyendraRathore | Ujjain, Madhya Pradesh |
| VOT1000000016 | Prabal Singh | PrabalSingh | Aligarh, Uttar Pradesh |
| VOT1000000017 | Akash Dwivedi | AkashDwivedi | Sidhi, Madhya Pradesh |
| VOT1000000018 | Priyanshu Raj | PriyanshuRaj | Bilaspur, Chhattisgarh |
| VOT1000000019 | Atul Pawar | AtulPawar | Pune, Maharashtra |
| VOT1000000020 | Himanshu Muleva | HimanshuMuleva | Indore, Madhya Pradesh |
| VOT1000000021 | Anuj Sharma | AnujSharma | Agra, Uttar Pradesh |
| VOT1000000022 | Akash Rout | AkashRout | Belagavi, Karnataka |
| VOT1000000023 | Pratinav | Pratinav | Varanasi, Uttar Pradesh |
| VOT1000000024 | Satyabrat Panigrahi | SatyabratPanigrahi | Bhadrak, Odisha |
| VOT1000000025 | Rohan Sinha | RohanSinha | Durg, Chhattisgarh |
| VOT1000000026 | Nishant Vij | NishantVij | Sirsa, Haryana |
| VOT1000000027 | Pritish Bhatiya | PritishBhatiya | Jabalpur, Madhya Pradesh |
| VOT1000000028 | Apoorv Singh | ApoorvSingh | Mathura, Uttar Pradesh |
| VOT1000000029 | Pranav Tiwari | PranavTiwari | Ghaziabad, Uttar Pradesh |
| VOT1000000030 | Abhijeet Singh | AbhijeetSingh | Indore, Madhya Pradesh |
| VOT3456789013 | Aakarsha Jobi | AakarshaJobi | Ernakulam, Kerala |
| VOT3456789014 | Aakash Gangwar | AakashGangwar | Meerut, Uttar Pradesh |
| VOT3456789016 | Aarti Kumari | AartiKumari | Patna, Bihar |
| VOT3456789017 | Aashish Malik | AashishMalik | Gurgaon, Haryana |
| VOT3456789018 | Aayush Singla | AayushSingla | Ludhiana, Punjab |
| VOT3456789019 | Abhijit Mandloi | AbhijitMandloi | Indore, Madhya Pradesh |
| VOT3456789020 | Abhishek Potbhare | AbhishekPotbhare | Nagpur, Maharashtra |
| VOT3456789021 | Abhishek Singh | AbhishekSingh | Lucknow, Uttar Pradesh |
| VOT3456789022 | Abhishek Verma | AbhishekVerma | Varanasi, Uttar Pradesh |
| VOT3456789023 | Adarsh Kamble | AdarshKamble | Pune, Maharashtra |
| VOT3456789024 | Adarsh Singh | AdarshSingh | Kanpur, Uttar Pradesh |
| VOT3456789025 | Adhyyan Chaturvedi | AdhyyanChaturvedi | Lucknow, Uttar Pradesh |
| VOT3456789026 | Aditya Bhosale | AdityaBhosale | Mumbai, Maharashtra |
| VOT3456789027 | Aditya Singh | AdityaSingh | Allahabad, Uttar Pradesh |
| VOT3456789028 | Aditya Ningadalli | AdityaNingadalli | Belgaum, Karnataka |
| VOT3456789029 | Aditya Shah | AdityaShah | Ahmedabad, Gujarat |
| VOT3456789030 | Adwaidh CR | AdwaidhCR | Thiruvananthapuram, Kerala |
| VOT3456789031 | Akash Dwivedi | AkashDwivedi | Lucknow, Uttar Pradesh |
| VOT3456789032 | Akhil Kothari | AkhilKothari | Jaipur, Rajasthan |
| VOT3456789033 | Akshay Uparikar | AkshayUparikar | Pune, Maharashtra |
| VOT3456789034 | Alok Singh | AlokSingh | Patna, Bihar |
| VOT3456789035 | Alok Randive | AlokRandive | Nashik, Maharashtra |
| VOT3456789036 | Aman | Aman | Delhi, Delhi |
| VOT3456789037 | Aman Kumar | AmanKumar | Patna, Bihar |
| VOT3456789038 | Anand Chaubey | AnandChaubey | Varanasi, Uttar Pradesh |
| VOT3456789039 | Anil Kumar | AnilKumar | Patna, Bihar |
| VOT3456789040 | Animesh Tyagi | AnimeshTyagi | Meerut, Uttar Pradesh |
| VOT3456789041 | Anirudh Deshmukh | AnirudhDeshmukh | Nagpur, Maharashtra |
| VOT3456789042 | Anjali Paramhans | AnjaliParamhans | Pune, Maharashtra |
| VOT3456789043 | Ankit Kumar | AnkitKumar | Patna, Bihar |
| VOT3456789044 | Ansh Jain | AnshJain | Indore, Madhya Pradesh |
| VOT3456789045 | Anshul Lodhi | AnshulLodhi | Bhopal, Madhya Pradesh |
| VOT3456789046 | Anubhav Chauhan | AnubhavChauhan | Dehradun, Uttarakhand |
| VOT3456789047 | Anurag Vinchurkar | AnuragVinchurkar | Nagpur, Maharashtra |
| VOT3456789048 | Anurag Tiwari | AnuragTiwari | Lucknow, Uttar Pradesh |
| VOT3456789049 | Anushri Sharma | AnushriSharma | Jaipur, Rajasthan |
| VOT3456789050 | Apoorv Singh | ApoorvSingh | Lucknow, Uttar Pradesh |
| VOT3456789051 | Arya Nanda | AryaNanda | Thiruvananthapuram, Kerala |
| VOT3456789052 | Ashi Kankane | AshiKankane | Indore, Madhya Pradesh |
| VOT3456789053 | Ashish Kothale | AshishKothale | Pune, Maharashtra |
| VOT3456789054 | Ashitosh Nandanwar | AshitoshNandanwar | Nagpur, Maharashtra |
| VOT3456789055 | Avadhut Joshi | AvadhutJoshi | Pune, Maharashtra |
| VOT3456789056 | Avu Kundana | AvuKundana | Hyderabad, Telangana |
| VOT3456789057 | Awinash Kumar | AwinashKumar | Patna, Bihar |
| VOT3456789058 | Ayush Kush | AyushKush | Patna, Bihar |
| VOT3456789059 | Ayush Soni | AyushSoni | Indore, Madhya Pradesh |
| VOT3456789060 | Ayush Tyagi | AyushTyagi | Meerut, Uttar Pradesh |
| VOT3456789061 | Ayush Verma | AyushVerma | Lucknow, Uttar Pradesh |
| VOT3456789062 | Bala Rao | BalaRao | Visakhapatnam, Andhra Pradesh |
| VOT3456789063 | Balaji P | BalajiP | Chennai, Tamil Nadu |
| VOT3456789064 | Bhavjeet Singh | BhavjeetSingh | Amritsar, Punjab |
| VOT3456789065 | Bhavna Agrawal | BhavnaAgrawal | Jaipur, Rajasthan |
| VOT3456789066 | Bora Reddy | BoraReddy | Hyderabad, Telangana |
| VOT3456789067 | C Achhaiyya | CAchhaiyya | Chennai, Tamil Nadu |
| VOT3456789068 | Chaudhari Jitendrakumar | ChaudhariJitendrakumar | Surat, Gujarat |
| VOT3456789069 | Chetan Choudhari | ChetanChoudhari | Pune, Maharashtra |
| VOT3456789070 | Daksh Shrivastava | DakshShrivastava | Lucknow, Uttar Pradesh |
| VOT3456789071 | Danish Husain | DanishHusain | Lucknow, Uttar Pradesh |
| VOT3456789072 | Deepak | Deepak | Delhi, Delhi |
| VOT3456789073 | Deepanshu Tiwari | DeepanshuTiwari | Lucknow, Uttar Pradesh |
| VOT3456789074 | Deepika Vishwakarma | DeepikaVishwakarma | Lucknow, Uttar Pradesh |
| VOT3456789075 | Devam Prajapati | DevamPrajapati | Ahmedabad, Gujarat |
| VOT3456789076 | Devansh Choudhary | DevanshChoudhary | Jaipur, Rajasthan |
| VOT3456789077 | Devansh Singh | DevanshSingh | Lucknow, Uttar Pradesh |
| VOT3456789078 | Devashish Ugale | DevashishUgale | Mumbai, Maharashtra |
| VOT3456789079 | Digambar Rathod | DigambarRathod | Pune, Maharashtra |
| VOT3456789080 | Diksha Kulkarni | DikshaKulkarni | Pune, Maharashtra |
| VOT3456789081 | Emandi Kumar | EmandiKumar | Visakhapatnam, Andhra Pradesh |
| VOT3456789082 | Gargi Hawaldar | GargiHawaldar | Pune, Maharashtra |
| VOT3456789083 | Gaurav Verma | GauravVerma | Lucknow, Uttar Pradesh |
| VOT3456789084 | Gauri Tyagi | GauriTyagi | Meerut, Uttar Pradesh |
| VOT3456789085 | Gottumukkala Varma | GottumukkalaVarma | Vijayawada, Andhra Pradesh |
| VOT3456789086 | Gourav Jain | GouravJain | Jaipur, Rajasthan |
| VOT3456789087 | Gulshan Anand | GulshanAnand | Delhi, Delhi |
| VOT3456789088 | Gunupati Reddy | GunupatiReddy | Hyderabad, Telangana |
| VOT3456789089 | Hardik | Hardik | Ahmedabad, Gujarat |
| VOT3456789090 | Harsh Yadav | HarshYadav | Lucknow, Uttar Pradesh |
| VOT3456789091 | Harshada Ghaywat | HarshadaGhaywat | Nagpur, Maharashtra |
| VOT3456789092 | Harshavardhan Latkar | HarshavardhanLatkar | Mumbai, Maharashtra |
| VOT3456789093 | Harshit Anjana | HarshitAnjana | Jaipur, Rajasthan |
| VOT3456789094 | Ishaan Garg | IshaanGarg | Delhi, Delhi |
| VOT3456789095 | Jay Sharma | JaySharma | Jaipur, Rajasthan |
| VOT3456789096 | Jinesh Chougule | JineshChougule | Kolhapur, Maharashtra |
| VOT3456789097 | Jitesh Mehta | JiteshMehta | Mumbai, Maharashtra |
| VOT3456789098 | Kanik Yadav | KanikYadav | Lucknow, Uttar Pradesh |
| VOT3456789099 | Kartikey Tyagi | KartikeyTyagi | Meerut, Uttar Pradesh |
| VOT3456789100 | Keval Makdiya | KevalMakdiya | Surat, Gujarat |
| VOT3456789101 | Kishan Singh | KishanSingh | Jaipur, Rajasthan |
| VOT3456789102 | Kolluri Renusree | KolluriRenusree | Vijayawada, Andhra Pradesh |
| VOT3456789103 | Komal Sonawane | KomalSonawane | Pune, Maharashtra |
| VOT3456789104 | Krishna Panale | KrishnaPanale | Pune, Maharashtra |
| VOT3456789105 | Kusuma Gadi | KusumaGadi | Hyderabad, Telangana |
| VOT3456789106 | Lakshit Chandrakar | LakshitChandrakar | Raipur, Chhattisgarh |
| VOT3456789107 | Lija Sahoo | LijaSahoo | Bhubaneswar, Odisha |
| VOT3456789108 | Maaz Javed | MaazJaved | Lucknow, Uttar Pradesh |
| VOT3456789109 | Madhur Chaudhari | MadhurChaudhari | Pune, Maharashtra |
| VOT3456789110 | Manohar Dudhat | ManoharDudhat | Ahmedabad, Gujarat |
| VOT3456789111 | Manoj MN | ManojMN | Bengaluru, Karnataka |
| VOT3456789112 | Mayank Bansal | MayankBansal | Delhi, Delhi |
| VOT3456789113 | Mayank Chaudhary | MayankChaudhary | Jaipur, Rajasthan |
| VOT3456789114 | Mayuri Narale | MayuriNarale | Pune, Maharashtra |
| VOT3456789115 | Md Zaman | MdZaman | Kolkata, West Bengal |
| VOT3456789116 | Mohammad Durrani | MohammadDurrani | Lucknow, Uttar Pradesh |
| VOT3456789117 | Mourya Konada | MouryaKonada | Hyderabad, Telangana |
| VOT3456789118 | Mousumi Das | MousumiDas | Kolkata, West Bengal |
| VOT3456789119 | Naina Yadav | NainaYadav | Lucknow, Uttar Pradesh |
| VOT3456789120 | Nentabaje Sakshi | NentabajeSakshi | Pune, Maharashtra |
| VOT3456789121 | Nikita Deshmukh | NikitaDeshmukh | Nagpur, Maharashtra |
| VOT3456789122 | Nikita Patel | NikitaPatel | Ahmedabad, Gujarat |
| VOT3456789123 | Nimisha Verma | NimishaVerma | Lucknow, Uttar Pradesh |
| VOT3456789124 | Niraj Gehlot | NirajGehlot | Jaipur, Rajasthan |
| VOT3456789125 | Nirupama Rajana | NirupamaRajana | Hyderabad, Telangana |
| VOT3456789126 | Nishant Vij | NishantVij | Delhi, Delhi |
| VOT3456789127 | Nishi Ranjan | NishiRanjan | Patna, Bihar |
| VOT3456789128 | Onkar Jogdand | OnkarJogdand | Pune, Maharashtra |
| VOT3456789129 | Palak Sirothiya | PalakSirothiya | Indore, Madhya Pradesh |
| VOT3456789130 | Pittala Soumya | PittalaSoumya | Vijayawada, Andhra Pradesh |
| VOT3456789131 | Prabhat Kumar | PrabhatKumar | Patna, Bihar |
| VOT3456789132 | Prajakta Derle | PrajaktaDerle | Pune, Maharashtra |
| VOT3456789133 | Prajwal Patil | PrajwalPatil | Mumbai, Maharashtra |
| VOT3456789134 | Prakhar Singh | PrakharSingh | Lucknow, Uttar Pradesh |
| VOT3456789135 | Pranav Nair | PranavNair | Kochi, Kerala |
| VOT3456789137 | Pranavi | Pranavi | Hyderabad, Telangana |
| VOT3456789138 | Pranjal Agrawal | PranjalAgrawal | Jaipur, Rajasthan |
| VOT3456789139 | Pratiksha Lande | PratikshaLande | Pune, Maharashtra |
| VOT3456789140 | Priyanshu Patidar | PriyanshuPatidar | Indore, Madhya Pradesh |
| VOT3456789141 | Priyanshu Raj | PriyanshuRaj | Patna, Bihar |
| VOT3456789142 | Pruthvi Bhat | PruthviBhat | Mangalore, Karnataka |
| VOT3456789143 | Raghavendra Uppar | RaghavendraUppar | Bengaluru, Karnataka |
| VOT3456789144 | Raj Payasi | RajPayasi | Jaipur, Rajasthan |
| VOT3456789145 | Rajat Mourya | RajatMourya | Lucknow, Uttar Pradesh |
| VOT3456789146 | Rajdeep Kala | RajdeepKala | Amritsar, Punjab |
| VOT3456789147 | Rajnish Singh | RajnishSingh | Patna, Bihar |
| VOT3456789148 | Ram Wandile | RamWandile | Pune, Maharashtra |
| VOT3456789149 | Ratnesh Raja | RatneshRaja | Patna, Bihar |
| VOT3456789151 | Rishi Singh | RishiSingh | Lucknow, Uttar Pradesh |
| VOT3456789152 | Ritesh Singh | RiteshSingh | Lucknow, Uttar Pradesh |
| VOT3456789153 | Ritik Bhattal | RitikBhattal | Chandigarh, Punjab |
| VOT3456789154 | Ronit Singh | RonitSingh | Lucknow, Uttar Pradesh |
| VOT3456789155 | Rupesh Patil | RupeshPatil | Mumbai, Maharashtra |
| VOT3456789157 | Rutuja Khot | RutujaKhot | Mumbai, Maharashtra |
| VOT3456789158 | S Pranav | SPranav | Chennai, Tamil Nadu |
| VOT3456789159 | Sahil Kadam | SahilKadam | Mumbai, Maharashtra |
| VOT3456789160 | Saket Bagre | SaketBagre | Indore, Madhya Pradesh |
| VOT3456789161 | Saksham Jain | SakshamJain | Jaipur, Rajasthan |
| VOT3456789162 | Sakshi Pimpale | SakshiPimpale | Pune, Maharashtra |
| VOT3456789163 | Sanskar Chaurasia | SanskarChaurasia | Lucknow, Uttar Pradesh |
| VOT3456789164 | Sarode Mamata | SarodeMamata | Pune, Maharashtra |
| VOT3456789165 | Sarthak Gore | SarthakGore | Pune, Maharashtra |
| VOT3456789166 | Satyam Patel | SatyamPatel | Surat, Gujarat |
| VOT3456789167 | Satyendra Rathore | SatyendraRathore | Jaipur, Rajasthan |
| VOT3456789168 | Saurabh Mali | SaurabhMali | Pune, Maharashtra |
| VOT3456789169 | Sayma Ahmad | SaymaAhmad | Lucknow, Uttar Pradesh |
| VOT3456789170 | Shivansh Mishra | ShivanshMishra | Lucknow, Uttar Pradesh |
| VOT3456789171 | Shreyansh Shah | ShreyanshShah | Ahmedabad, Gujarat |
| VOT3456789172 | Shristi Mishra | ShristiMishra | Lucknow, Uttar Pradesh |
| VOT3456789173 | Shubham Dhakate | ShubhamDhakate | Nagpur, Maharashtra |
| VOT3456789174 | Shubham Kumar | ShubhamKumar | Patna, Bihar |
| VOT3456789175 | Shubham Rahangdale | ShubhamRahangdale | Nagpur, Maharashtra |
| VOT3456789176 | Shubhra Tiwari | ShubhraTiwari | Lucknow, Uttar Pradesh |
| VOT3456789178 | Sonit Nagpure | SonitNagpure | Nagpur, Maharashtra |
| VOT3456789179 | Soumya Bharti | SoumyaBharti | Patna, Bihar |
| VOT3456789180 | Soumya Singh | SoumyaSingh | Lucknow, Uttar Pradesh |
| VOT3456789181 | Sriram S | SriramS | Chennai, Tamil Nadu |
| VOT3456789182 | Stalin Kanjiramparambil | StalinKanjiramparambil | Kochi, Kerala |
| VOT3456789183 | Sumit Kumar | SumitKumar | Patna, Bihar |
| VOT3456789184 | Sumit Sharma | SumitSharma | Jaipur, Rajasthan |
| VOT3456789185 | Suyesh Dubey | SuyeshDubey | Lucknow, Uttar Pradesh |
| VOT3456789186 | Suyog Tathe | SuyogTathe | Pune, Maharashtra |
| VOT3456789187 | Swarnil Kokulwar | SwarnilKokulwar | Nagpur, Maharashtra |
| VOT3456789188 | Swathi Simbothula | SwathiSimbothula | Hyderabad, Telangana |
| VOT3456789189 | Swati Kumari | SwatiKumari | Patna, Bihar |
| VOT3456789190 | Sweekar Chougale | SweekarChougale | Belgaum, Karnataka |
| VOT3456789191 | Tanishq Jarsodiwala | TanishqJarsodiwala | Mumbai, Maharashtra |
| VOT3456789192 | Tanuja Ghaytidak | TanujaGhaytidak | Pune, Maharashtra |
| VOT3456789193 | Tanvi Gundarwar | TanviGundarwar | Nagpur, Maharashtra |
| VOT3456789194 | Tanya Naidu | TanyaNaidu | Hyderabad, Telangana |
| VOT3456789195 | Titiksha Chandrakar | TitikshaChandrakar | Raipur, Chhattisgarh |
| VOT3456789196 | Tushar Patil | TusharPatil | Mumbai, Maharashtra |
| VOT3456789197 | Ujjwal Chourasia | UjjwalChourasia | Lucknow, Uttar Pradesh |
| VOT3456789199 | Vaishnav Uke | VaishnavUke | Pune, Maharashtra |
| VOT3456789200 | Vansh Dhiman | VanshDhiman | Chandigarh, Punjab |
| VOT3456789201 | Vibhor Jayaswal | VibhorJayaswal | Lucknow, Uttar Pradesh |
| VOT3456789202 | Vikhyat Gupta | VikhyatGupta | Delhi, Delhi |
| VOT3456789203 | Vishal | Vishal | Delhi, Delhi |
| VOT3456789204 | Vishal Rawat | VishalRawat | Dehradun, Uttarakhand |
| VOT3456789206 | Yash Dudhe | YashDudhe | Nagpur, Maharashtra |
| VOT3456789207 | Yash Mourya | YashMourya | Lucknow, Uttar Pradesh |
| VOT3456789208 | Yash Shirude | YashShirude | Mumbai, Maharashtra |
| VOT3456789209 | Yash Sonkuwar | YashSonkuwar | Nagpur, Maharashtra |