export interface UserProfile {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
}

export interface UpdateProfileRequest {
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string | null;
}

export interface MessageResponse {
  message: string;
}
