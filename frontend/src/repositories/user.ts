export class User {
  public id?: number;
  public email?: string;
  public firstName?: string;
  public lastName?: string;
  public username?: string;
  public phone?: string;
  public role?: string;
  public avatarUrl?: string
  public uploadNumber?: number;
  public downloadNumber?: number;

  public static buildUser(user: any): User {
    const newUser = new User();
    
    newUser.id = user?.id;
    newUser.email = user.email;
    newUser.firstName = user.firstName;
    newUser.lastName = user.lastName;
    newUser.username = user.username;
    newUser.phone = user.phone;
    newUser.role = user.role;
    newUser.avatarUrl = user.avatarUrl
    newUser.uploadNumber = user.uploadNumber
    newUser.downloadNumber = user.downloadNumber

    return newUser;
  }

  public isAdmin(): boolean {
    return this.role === ROLE_TYPE.ADMIN;
  }
}

export const ROLE_TYPE = {
  ADMIN: 'ROLE_ADMIN',
  USER: 'ROLE_USER',
};
