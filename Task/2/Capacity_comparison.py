
import matplotlib.pyplot as plt



k = [2:10]
C            = [   10,    15,    20, 25]
C_VWP        = [99999, 99999,   228,228]
C_WP         = [                320,304]   # MANY OBJ FUNC
C_WP_2       = [         228,   320,304]   # min(sum(r_ncw))

C_Splittable = [99999,174.13,155.51,228]  # OPTIMUM

# C_WP_2 : C=15 -> traffic lost
#          C=20 -> traffic lost
#          C=25 -> traffic lost

fig = plt.figure()
font = {'family' : 'sans', 'size'   : 12}
plt.rc('font', **font)

plt.plot(C[1:], C_Splittable[1:], 'bo-', color='red', label='')
plt.plot(C[4:], C_Unsplittable[4:], 'bo-', color='blue', label='')
#plt.yscale('log')
plt.title('CAPACITY COMPARISON')
plt.xlabel('Link Capacity')
plt.ylabel('Total carried traffic')
plt.grid(which='major', axis='both', linestyle='--')

for x_,y_ in zip(C[1:],C_Splittable[1:]):
    label = round(y_, 2)
    plt.annotate(label, # this is the text
                 (x_,y_), # this is the point to label
                 textcoords="offset points", # how to position the text
                 xytext=(0,10), # distance from text to points (x,y)
                 ha='center',
                 rotation = 45,
                 fontsize = 8) # horizontal alignment can be left, right or center

for x_,y_ in zip(C[4:],C_Unsplittable[4:]):
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




