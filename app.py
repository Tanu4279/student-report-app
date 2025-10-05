# student_report_app.py
import streamlit as st
import pandas as pd
import os

# ==================================================
# 🎨 THEME TOGGLE
# ==================================================
st.sidebar.markdown("### 🎨 Theme Settings")
theme = st.sidebar.radio("Choose Theme:", ["🌞 Light", "🌙 Dark"])

if theme == "🌙 Dark":
    st.markdown("""
        <style>
        .stApp { background-color: #121212; color: #FFFFFF; }
        h1, h2, h3, h4, h5, h6 { color: #FFFFFF !important; }
        p, span, div { color: #EAEAEA !important; }
        .stDataFrame, .stTable { background-color: #1E1E1E !important; color: #FFFFFF !important; }
        section[data-testid="stSidebar"] { background-color: #1a1a1a; color: #FFFFFF; }
        .stTextInput input, .stNumberInput input { background-color: #2d2d2d; color: #FFFFFF !important; }
        div.stButton > button:first-child { background-color: #0072B2; color: white; }
        </style>
    """, unsafe_allow_html=True)
else:
    st.markdown("""
        <style>
        body { background-color: white; color: black; }
        .stApp { background-color: white; color: black; }
        .stDataFrame, .stTable { background-color: #f9f9f9; color: black; }
        </style>
    """, unsafe_allow_html=True)

# ==================================================
# 📁 FILE HANDLING
# ==================================================
FILE = "students.csv"

if not os.path.exists(FILE):
    df = pd.DataFrame(columns=["RollNo", "Name", "Physics", "Chemistry", "Maths", "English", "CS", "Percentage", "Grade"])
    df.to_csv(FILE, index=False)

def load_data():
    return pd.read_csv(FILE)

def save_data(df):
    df.to_csv(FILE, index=False)

def calculate_grade(p, c, m, e, cs):
    per = (p + c + m + e + cs) / 5.0
    if per >= 60: grade = "A"
    elif per >= 50: grade = "B"
    elif per >= 33: grade = "C"
    else: grade = "F"
    return per, grade

# ==================================================
# 🧾 MAIN APP
# ==================================================
st.title("📘 Student Report Card System")
st.caption("Made by: Tanu")

menu = [
    "🏠 Home",
    "➕ Add Student",
    "📋 All Records",
    "🔍 Search Student",
    "✏️ Modify Student",
    "🗑️ Delete Student",
    "📊 Class Result"
]
choice = st.sidebar.radio("Menu", menu)

# --------------------------------------------------
# 🏠 HOME
# --------------------------------------------------
if choice == "🏠 Home":
    st.subheader("Welcome to Student Report Card System")
    st.markdown("This **Streamlit version** is based on the Java Report Card project.")
    st.info("Use the sidebar to navigate between features.")

# --------------------------------------------------
# ➕ ADD STUDENT
# --------------------------------------------------
elif choice == "➕ Add Student":
    st.subheader("Add New Student Record")
    roll = st.number_input("Enter Roll Number", min_value=1, step=1)
    name = st.text_input("Enter Student Name")
    p = st.number_input("Marks in Physics (0–100)", 0, 100)
    c = st.number_input("Marks in Chemistry (0–100)", 0, 100)
    m = st.number_input("Marks in Maths (0–100)", 0, 100)
    e = st.number_input("Marks in English (0–100)", 0, 100)
    cs = st.number_input("Marks in Computer Science (0–100)", 0, 100)

    if st.button("💾 Save Record"):
        df = load_data()
        if roll in df["RollNo"].values:
            st.error("⚠️ Roll number already exists!")
        else:
            per, grade = calculate_grade(p, c, m, e, cs)
            new_row = pd.DataFrame([[roll, name, p, c, m, e, cs, per, grade]], columns=df.columns)
            df = pd.concat([df, new_row], ignore_index=True)
            save_data(df)
            st.success("✅ Record added successfully!")

# --------------------------------------------------
# 📋 ALL RECORDS
# --------------------------------------------------
elif choice == "📋 All Records":
    st.subheader("All Student Records")
    df = load_data()
    if df.empty:
        st.warning("No records found!")
    else:
        st.dataframe(df)

# --------------------------------------------------
# 🔍 SEARCH STUDENT
# --------------------------------------------------
elif choice == "🔍 Search Student":
    st.subheader("Search Student Record")
    roll = st.number_input("Enter Roll Number to Search", min_value=1, step=1)
    if st.button("🔎 Search"):
        df = load_data()
        student = df[df["RollNo"] == roll]
        if student.empty:
            st.error("❌ Record not found!")
        else:
            st.success("✅ Record found!")
            st.table(student)

# --------------------------------------------------
# ✏️ MODIFY STUDENT
# --------------------------------------------------
elif choice == "✏️ Modify Student":
    st.subheader("Modify Student Record")
    roll = st.number_input("Enter Roll Number to Modify", min_value=1, step=1)
    df = load_data()
    student = df[df["RollNo"] == roll]

    if not student.empty:
        st.write("### Current Record:")
        st.table(student)

        name = st.text_input("New Name", student.iloc[0]["Name"])
        p = st.number_input("Physics", 0, 100, int(student.iloc[0]["Physics"]))
        c = st.number_input("Chemistry", 0, 100, int(student.iloc[0]["Chemistry"]))
        m = st.number_input("Maths", 0, 100, int(student.iloc[0]["Maths"]))
        e = st.number_input("English", 0, 100, int(student.iloc[0]["English"]))
        cs = st.number_input("Computer Science", 0, 100, int(student.iloc[0]["CS"]))

        if st.button("✅ Update Record"):
            per, grade = calculate_grade(p, c, m, e, cs)
            df.loc[df["RollNo"] == roll, ["Name", "Physics", "Chemistry", "Maths", "English", "CS", "Percentage", "Grade"]] = \
                [name, p, c, m, e, cs, per, grade]
            save_data(df)
            st.success("🎉 Record updated successfully!")
    else:
        st.warning("Enter a valid Roll Number.")

# --------------------------------------------------
# 🗑️ DELETE STUDENT
# --------------------------------------------------
elif choice == "🗑️ Delete Student":
    st.subheader("Delete Student Record")
    roll = st.number_input("Enter Roll Number to Delete", min_value=1, step=1)
    if st.button("🗑️ Delete"):
        df = load_data()
        if roll in df["RollNo"].values:
            df = df[df["RollNo"] != roll]
            save_data(df)
            st.success("🧾 Record deleted successfully!")
        else:
            st.error("❌ Record not found!")

# --------------------------------------------------
# 📊 CLASS RESULT
# --------------------------------------------------
elif choice == "📊 Class Result":
    st.subheader("Class Result (All Students)")
    df = load_data()
    if df.empty:
        st.warning("No records found!")
    else:
        df_sorted = df.sort_values(by="Percentage", ascending=False)
        st.dataframe(df_sorted[["RollNo", "Name", "Percentage", "Grade"]])
