
import matplotlib.pyplot as plt




C            = [   10,    15,    18,   19,   20, 25]
C_VWP        = [99999, 99999,   228,  228,  228,228]
C_WP         = [99999, 99999, 99999,99999,  228,228]   

C_Splittable = [99999,174.13,155.51,228]  # OPTIMUM



fig = plt.figure()
font = {'family' : 'sans', 'size'   : 12}
plt.rc('font', **font)

plt.plot(C[3:], C_VWP[3:], 'bo-', color='red', label='RF_VWP')
plt.plot(C[4:], C_WP[4:], 'bo-', color='blue', label='RF_WP')
#plt.yscale('log')
plt.title('CAPACITY COMPARISON RF')
plt.xlabel('Link Capacity')
plt.ylabel('Total carried traffic')
plt.grid(which='major', axis='both', linestyle='--')
plt.xlim([10, 30])

for x_,y_ in zip(C[3:],C_VWP[3:]):
    label = round(y_, 2)
    plt.annotate(label, # this is the text
                 (x_,y_), # this is the point to label
                 textcoords="offset points", # how to position the text
                 xytext=(0,10), # distance from text to points (x,y)
                 ha='center',
                 rotation = 45,
                 fontsize = 8) # horizontal alignment can be left, right or center

for x_,y_ in zip(C[4:],C_WP[4:]):
    label = round(y_, 2)
    plt.annotate(label, # this is the text
                 (x_,y_), # this is the point to label
                 textcoords="offset points", # how to position the text
                 xytext=(0,10), # distance from text to points (x,y)
                 ha='center',
                 rotation = 45,
                 fontsize = 8) # horizontal alignment can be left, right or center


plt.legend()
plt.show()




