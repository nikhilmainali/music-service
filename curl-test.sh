#!/bin/bash
# ==============================================================================
# Music & People Discovery Service - Interactive API Demo Script
#
# Make sure the service is running first:
#   ./mvnw spring-boot:run
#
# Then run this script in another terminal:
#   chmod +x curl-test.sh
#   ./curl-test.sh
# ==============================================================================

BASE_URL="http://localhost:8080"
GREEN='\033[0;32m'
CYAN='\033[0;36m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${CYAN}======================================================${NC}"
echo -e "${CYAN}   Music & People Discovery REST API Demo             ${NC}"
echo -e "${CYAN}======================================================${NC}\n"

# 1. Health & Catalog check
echo -e "${YELLOW}1. GET /music (List entire music catalog)${NC}"
curl -s -X GET "${BASE_URL}/music" | jq .
echo -e "\n"

echo -e "${YELLOW}2. GET /music/1 (Get single song: Daft Punk)${NC}"
curl -s -X GET "${BASE_URL}/music/1" | jq .
echo -e "\n"

echo -e "${YELLOW}2b. GET /music/random (Surprise Me! Get random song)${NC}"
curl -s -X GET "${BASE_URL}/music/random" | jq .
echo -e "\n"

# 2. Create a new song
echo -e "${YELLOW}3. POST /music (Add Pink Floyd - Time)${NC}"
NEW_SONG=$(curl -s -X POST "${BASE_URL}/music" \
  -H "Content-Type: application/json" \
  -d '{
    "artist-name": "Pink Floyd",
    "genre-name": "Progressive Rock",
    "link": "https://open.spotify.com/track/3TO7bbrV2Vp0wO8iG3bpqA"
  }')
echo $NEW_SONG | jq .
NEW_SONG_ID=$(echo $NEW_SONG | jq '.id')
echo -e "Created song with ID: ${GREEN}${NEW_SONG_ID}${NC}\n"

# 3. Patch the new song
echo -e "${YELLOW}4. PATCH /music/${NEW_SONG_ID} (Update genre to 'Classic Rock')${NC}"
curl -s -X PATCH "${BASE_URL}/music/${NEW_SONG_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "genre-name": "Classic Rock"
  }' | jq .
echo -e "\n"

# 4. User endpoints
echo -e "${YELLOW}5. GET /user (List all users and their music IDs)${NC}"
curl -s -X GET "${BASE_URL}/user" | jq .
echo -e "\n"

echo -e "${YELLOW}6. GET /user/1 (Get Alice's profile and music list)${NC}"
curl -s -X GET "${BASE_URL}/user/1" | jq .
echo -e "\n"

echo -e "${YELLOW}7. POST /user (Create new user: Frank)${NC}"
NEW_USER=$(curl -s -X POST "${BASE_URL}/user" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Frank"
  }')
echo $NEW_USER | jq .
NEW_USER_ID=$(echo $NEW_USER | jq '.id')
echo -e "Created user with ID: ${GREEN}${NEW_USER_ID}${NC}\n"

# 5. Patch user (add song to user)
echo -e "${YELLOW}8. PATCH /user/${NEW_USER_ID} (Add song 1 to Frank's library)${NC}"
curl -s -X PATCH "${BASE_URL}/user/${NEW_USER_ID}" \
  -H "Content-Type: application/json" \
  -d '{
    "music": "1"
  }' | jq .
echo -e "\n"

# 6. Discovery endpoints
echo -e "${YELLOW}9. GET /user/1/similar-users (People Discovery for Alice)${NC}"
curl -s -X GET "${BASE_URL}/user/1/similar-users" | jq .
echo -e "\n"

echo -e "${YELLOW}10. GET /user/1/recommendations (Music Discovery for Alice)${NC}"
curl -s -X GET "${BASE_URL}/user/1/recommendations" | jq .
echo -e "\n"

echo -e "${YELLOW}11. GET /music/1/fans (Song Fans Discovery for Daft Punk)${NC}"
curl -s -X GET "${BASE_URL}/music/1/fans" | jq .
echo -e "\n"

# 7. Delete endpoints
echo -e "${YELLOW}12. DELETE /user/${NEW_USER_ID}/music/1 (Remove song from Frank)${NC}"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "${BASE_URL}/user/${NEW_USER_ID}/music/1"
echo -e "\n"

echo -e "${YELLOW}13. DELETE /user/${NEW_USER_ID} (Delete Frank)${NC}"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "${BASE_URL}/user/${NEW_USER_ID}"
echo -e "\n"

echo -e "${YELLOW}14. DELETE /music/${NEW_SONG_ID} (Delete Pink Floyd song)${NC}"
curl -s -w "HTTP Status: %{http_code}\n" -X DELETE "${BASE_URL}/music/${NEW_SONG_ID}"
echo -e "\n"

echo -e "${GREEN}✓ All demo endpoints executed successfully!${NC}"
