package com.nammaskill.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nammaskill.app.data.model.Application
import com.nammaskill.app.data.model.Course
import com.nammaskill.app.data.model.SkillCenter
import com.nammaskill.app.data.model.SuccessStory
import kotlinx.coroutines.tasks.await

object FirebaseRepository {

    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    // ─── Courses ──────────────────────────────────────────────────────────────

    suspend fun getCourses(): List<Course> = try {
        db.collection("courses").get().await()
            .toObjects(Course::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun getCourseById(id: String): Course? = try {
        db.collection("courses").document(id).get().await()
            .toObject(Course::class.java)
    } catch (e: Exception) { null }

    suspend fun getCoursesByTrade(trade: String): List<Course> = try {
        db.collection("courses").whereEqualTo("trade", trade).get().await()
            .toObjects(Course::class.java)
    } catch (e: Exception) { emptyList() }

    // ─── Skill Centers ────────────────────────────────────────────────────────

    suspend fun getSkillCenters(): List<SkillCenter> = try {
        db.collection("skillCenters").get().await()
            .toObjects(SkillCenter::class.java)
    } catch (e: Exception) { emptyList() }

    // ─── Success Stories ──────────────────────────────────────────────────────

    suspend fun getSuccessStories(): List<SuccessStory> = try {
        db.collection("successStories").get().await()
            .toObjects(SuccessStory::class.java)
    } catch (e: Exception) { emptyList() }

    // ─── Applications ─────────────────────────────────────────────────────────

    suspend fun submitApplication(application: Application): Result<String> = try {
        val ref = db.collection("applications").add(application.toMap()).await()
        Result.success(ref.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserApplications(phone: String): List<Application> = try {
        db.collection("applications")
            .whereEqualTo("phone", phone)
            .get().await()
            .toObjects(Application::class.java)
    } catch (e: Exception) { emptyList() }

    suspend fun saveInterest(courseId: String, phone: String): Result<Unit> = try {
        db.collection("interests").add(
            mapOf("courseId" to courseId, "phone" to phone, "timestamp" to System.currentTimeMillis())
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveCallbackRequest(courseId: String, phone: String, name: String): Result<Unit> = try {
        db.collection("callbackRequests").add(
            mapOf(
                "courseId" to courseId,
                "phone" to phone,
                "name" to name,
                "timestamp" to System.currentTimeMillis()
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ─── Seed Data ────────────────────────────────────────────────────────────

    suspend fun seedInitialData() {
        try {
            val coursesSnap = db.collection("courses").limit(1).get().await()
            if (!coursesSnap.isEmpty) return // already seeded

            seedCourses()
            seedSkillCenters()
            seedSuccessStories()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun seedCourses() {
        val courses = listOf(
            Course(
                title = "Electrical Wiring Technician",
                trade = "Electrician",
                centerName = "NSDC Dharwad Center",
                district = "Dharwad",
                duration = 2,
                durationType = "short",
                startDate = "01 Feb 2025",
                seatsAvailable = 20,
                totalSeats = 25,
                eligibility = "8th Pass, Age 18-35",
                jobGuaranteed = true,
                stipend = 1500,
                description = "Learn complete electrical wiring for homes and commercial buildings. Covers safety protocols, circuit design, and hands-on installation practice.",
                trainerName = "Ramesh Kumar",
                trainerContact = "9876543210",
                tags = listOf("electrical", "wiring", "government", "stipend")
            ),
            Course(
                title = "Advanced Welding & Fabrication",
                trade = "Welding",
                centerName = "Karnataka Skill Hub Belgaum",
                district = "Belgaum",
                duration = 4,
                durationType = "long",
                startDate = "15 Feb 2025",
                seatsAvailable = 15,
                totalSeats = 20,
                eligibility = "10th Pass, Age 18-40",
                jobGuaranteed = true,
                stipend = 2000,
                description = "Advanced welding techniques including MIG, TIG, and arc welding. Fabrication of metal structures for industrial use.",
                trainerName = "Suresh Patil",
                trainerContact = "9845678901",
                tags = listOf("welding", "fabrication", "industrial", "stipend")
            ),
            Course(
                title = "Smartphone Repair & Servicing",
                trade = "Mobile Repair",
                centerName = "Digital Skill Center Hubli",
                district = "Hubli",
                duration = 2,
                durationType = "short",
                startDate = "01 Mar 2025",
                seatsAvailable = 25,
                totalSeats = 30,
                eligibility = "8th Pass, Age 17-35",
                jobGuaranteed = false,
                stipend = 0,
                description = "Hands-on training in smartphone hardware and software repair. Covers iOS and Android devices, component replacement, and software flashing.",
                trainerName = "Vikram Singh",
                trainerContact = "9632145870",
                tags = listOf("mobile", "repair", "smartphone", "technology")
            ),
            Course(
                title = "Garment Making & Fashion Design",
                trade = "Sewing",
                centerName = "Women Skill Academy Davangere",
                district = "Davangere",
                duration = 3,
                durationType = "short",
                startDate = "10 Feb 2025",
                seatsAvailable = 30,
                totalSeats = 35,
                eligibility = "8th Pass, Age 18-40, Women preferred",
                jobGuaranteed = false,
                stipend = 1000,
                description = "Learn garment making, pattern cutting, stitching, and fashion design. Includes training on industrial sewing machines and boutique management.",
                trainerName = "Meena Lakshmi",
                trainerContact = "9741258630",
                tags = listOf("sewing", "fashion", "garment", "women", "stipend")
            ),
            Course(
                title = "Python Programming Basics",
                trade = "Coding",
                centerName = "Tech Skill Hub Mysore",
                district = "Mysore",
                duration = 6,
                durationType = "long",
                startDate = "05 Mar 2025",
                seatsAvailable = 20,
                totalSeats = 25,
                eligibility = "12th Pass / Graduate, Age 18-30",
                jobGuaranteed = true,
                stipend = 0,
                description = "Learn Python programming from scratch. Covers data structures, OOP, web scraping, and introduction to data science and ML concepts.",
                trainerName = "Priya Sharma",
                trainerContact = "9087654321",
                tags = listOf("coding", "python", "programming", "IT", "tech")
            ),
            Course(
                title = "Plumbing & Sanitation Work",
                trade = "Plumbing",
                centerName = "Construction Skill Center Mangalore",
                district = "Mangalore",
                duration = 2,
                durationType = "short",
                startDate = "20 Feb 2025",
                seatsAvailable = 12,
                totalSeats = 15,
                eligibility = "8th Pass, Age 18-40",
                jobGuaranteed = false,
                stipend = 1800,
                description = "Complete plumbing and sanitation training. Covers pipe fitting, drainage systems, bathroom fittings, and maintenance for residential and commercial projects.",
                trainerName = "Anand Nayak",
                trainerContact = "9512345678",
                tags = listOf("plumbing", "sanitation", "construction", "stipend")
            ),
            Course(
                title = "Furniture Making & Carpentry",
                trade = "Carpentry",
                centerName = "Craft Skill Center Hassan",
                district = "Hassan",
                duration = 5,
                durationType = "long",
                startDate = "01 Apr 2025",
                seatsAvailable = 10,
                totalSeats = 12,
                eligibility = "8th Pass, Age 18-45",
                jobGuaranteed = false,
                stipend = 0,
                description = "Learn furniture making, wood carving, and carpentry skills. Covers hand tools, power tools, design principles, and finishing techniques.",
                trainerName = "Raju Carpeter",
                trainerContact = "9357924680",
                tags = listOf("carpentry", "furniture", "woodwork", "craft")
            ),
            Course(
                title = "Solar Panel Installation",
                trade = "Electrician",
                centerName = "Green Energy Skill Hub Shimoga",
                district = "Shimoga",
                duration = 3,
                durationType = "short",
                startDate = "15 Mar 2025",
                seatsAvailable = 18,
                totalSeats = 20,
                eligibility = "10th Pass, Age 18-35",
                jobGuaranteed = true,
                stipend = 2500,
                description = "Specialized training in solar panel installation, maintenance, and troubleshooting. Covers rooftop and ground-mounted systems for residential and commercial use.",
                trainerName = "Naveen Solar",
                trainerContact = "9246813579",
                tags = listOf("solar", "renewable", "energy", "electrician", "stipend")
            )
        )
        val batch = db.batch()
        courses.forEach { course ->
            val ref = db.collection("courses").document()
            batch.set(ref, course.toMap())
        }
        batch.commit().await()
    }

    private suspend fun seedSkillCenters() {
        val centers = listOf(
            SkillCenter(
                name = "NSDC Dharwad Center",
                address = "Near Bus Stand, PB Road, Dharwad - 580001",
                district = "Dharwad",
                lat = 15.4589, lng = 75.0078,
                phone = "0836-2447890",
                tradesOffered = listOf("Electrician", "Plumbing", "Welding")
            ),
            SkillCenter(
                name = "Karnataka Skill Hub Belgaum",
                address = "Khanapur Road, Camp Area, Belgaum - 590001",
                district = "Belgaum",
                lat = 15.8497, lng = 74.4977,
                phone = "0831-2405678",
                tradesOffered = listOf("Welding", "Carpentry", "Electrician")
            ),
            SkillCenter(
                name = "Digital Skill Center Hubli",
                address = "Gokul Road, Vidyanagar, Hubli - 580031",
                district = "Hubli",
                lat = 15.3647, lng = 75.1240,
                phone = "0836-2353456",
                tradesOffered = listOf("Mobile Repair", "Coding", "Electrician")
            ),
            SkillCenter(
                name = "Tech Skill Hub Mysore",
                address = "Vijayanagar, 4th Stage, Mysore - 570017",
                district = "Mysore",
                lat = 12.2958, lng = 76.6394,
                phone = "0821-2519870",
                tradesOffered = listOf("Coding", "Mobile Repair", "Electrician")
            ),
            SkillCenter(
                name = "Construction Skill Center Mangalore",
                address = "Bejai, Near MRPL Gate, Mangalore - 575004",
                district = "Mangalore",
                lat = 12.8698, lng = 74.8432,
                phone = "0824-2234567",
                tradesOffered = listOf("Plumbing", "Welding", "Carpentry")
            ),
            SkillCenter(
                name = "Craft Skill Center Hassan",
                address = "BM Road, Shanthigrama, Hassan - 573201",
                district = "Hassan",
                lat = 13.0068, lng = 76.1004,
                phone = "08172-268901",
                tradesOffered = listOf("Carpentry", "Sewing", "Plumbing")
            )
        )
        val batch = db.batch()
        centers.forEach { center ->
            val ref = db.collection("skillCenters").document()
            batch.set(ref, center.toMap())
        }
        batch.commit().await()
    }

    private suspend fun seedSuccessStories() {
        val stories = listOf(
            SuccessStory(
                name = "Raju M.", age = 24, village = "Kundgol", district = "Dharwad",
                trade = "Welding",
                beforeStory = "Was unemployed for 2 years after 10th standard. Family struggling with debt.",
                afterStory = "Completed Advanced Welding course and placed at Tata Motors, Dharwad plant.",
                currentSalary = 22000,
                centerName = "NSDC Dharwad Center",
                quote = "This course changed my life completely. Now I support my entire family!"
            ),
            SuccessStory(
                name = "Meena B.", age = 22, village = "Channagiri", district = "Davangere",
                trade = "Sewing",
                beforeStory = "School dropout, stayed at home helping with household chores.",
                afterStory = "Started her own tailoring shop after the Garment Making course. Has 3 employees now.",
                currentSalary = 15000,
                centerName = "Women Skill Academy Davangere",
                quote = "I never thought I could run my own business. NammaSkill made it possible!"
            ),
            SuccessStory(
                name = "Suresh K.", age = 26, village = "Kalghatgi", district = "Hubli",
                trade = "Mobile Repair",
                beforeStory = "Used to work at a small grocery store earning ₹5,000/month.",
                afterStory = "Opened his own mobile repair shop in Hubli. Has 2 employees and expanding.",
                currentSalary = 25000,
                centerName = "Digital Skill Center Hubli",
                quote = "The hands-on training was excellent. I could open my shop within 3 months!"
            ),
            SuccessStory(
                name = "Kavitha R.", age = 23, village = "Gokak", district = "Belgaum",
                trade = "Coding",
                beforeStory = "Graduate but couldn't get a job for 1 year due to lack of technical skills.",
                afterStory = "Got placed in an IT company in Pune after the Python Programming course.",
                currentSalary = 35000,
                centerName = "Tech Skill Hub Mysore",
                quote = "From a small village in Belgaum to a tech job in Pune — never imagined this!"
            ),
            SuccessStory(
                name = "Mahesh S.", age = 28, village = "Nanjangud", district = "Mysore",
                trade = "Electrician",
                beforeStory = "Daily wage laborer earning irregular income with no stability.",
                afterStory = "Now works as a BESCOM contractor handling government projects independently.",
                currentSalary = 20000,
                centerName = "NSDC Dharwad Center",
                quote = "Stable income and respect in the community — that's what skill training gave me."
            ),
            SuccessStory(
                name = "Lakshmi H.", age = 25, village = "Belur", district = "Hassan",
                trade = "Plumbing",
                beforeStory = "Struggled to find work as a woman in male-dominated job market.",
                afterStory = "Became Karnataka's first female government plumbing contractor in Hassan district.",
                currentSalary = 18000,
                centerName = "Craft Skill Center Hassan",
                quote = "They said women can't do plumbing. I proved them all wrong!"
            )
        )
        val batch = db.batch()
        stories.forEach { story ->
            val ref = db.collection("successStories").document()
            batch.set(ref, story.toMap())
        }
        batch.commit().await()
    }
}
